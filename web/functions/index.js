const functions = require("firebase-functions");
const admin = require("firebase-admin");
const crypto = require("crypto");
const QRCode = require("qrcode");
const express = require("express");
const cors = require("cors");

admin.initializeApp();
const db = admin.firestore();
const {FieldValue} = require("firebase-admin/firestore");

const app = express();

// CORS apenas para sites permitidos
const allowedOrigins = ["http://localhost:5000"];
app.use(cors({
  origin: function(origin, callback) {
    if (!origin || allowedOrigins.includes(origin)) {
      callback(null, true);
    } else {
      callback(new Error("Not allowed by CORS"));
    }
  },
}));

app.use(express.json());

app.post("/", async (req, res) => {
  try {
    const {apiKey, siteUrl} = req.body;

    if (!apiKey || !siteUrl) {
      return res.status(400).json({
        error: "apiKey e siteUrl são obrigatórios.",
      });
    }

    const partnerDoc = await db.collection("partners").doc(siteUrl).get();
    if (!partnerDoc.exists || partnerDoc.data().apiKey !== apiKey) {
      return res.status(403).json({
        error: "Parceria inválida ou chave incorreta.",
      });
    }

    const loginToken = crypto.randomBytes(128).toString("hex");

    await db.collection("login").doc(loginToken).set({
      apiKey,
      createdAt: FieldValue.serverTimestamp(),
      loginToken,
    });

    const qrDataURL = await QRCode.toDataURL(loginToken);

    return res.status(200).json({
      qrCodeBase64: qrDataURL,
      loginToken: loginToken,
    });
  } catch (error) {
    console.error("Erro na performAuth:", error);
    return res.status(500).json({error: "Erro interno no servidor."});
  }
});

app.post("/getLoginStatus", async (req, res) => {
  try {
    if (req.method !== "POST") {
      return res.status(405).json({error: "Método não permitido"});
    }
    const {loginToken} = req.body;
    if (!loginToken) {
      return res.status(400).json({error: "loginToken obrigatório"});
    }
    const loginDocRef = db.collection("login").doc(loginToken);
    const loginDoc = await loginDocRef.get();
    if (!loginDoc.exists) {
      return res.status(404).json({
        error: "loginToken não encontrado ou expirado",
      });
    }
    const docData = loginDoc.data();
    const now = Date.now();
    const createdAt = docData.createdAt ? docData.createdAt.toMillis() : 0;
    if (now - createdAt > 60000) {
      await loginDocRef.delete();
      return res.status(410).json({error: "loginToken expirado"});
    }
    let count = docData.checkCount || 0;
    if (count >= 3) {
      await loginDocRef.delete();
      return res.status(410).json({
        error: "Número máximo de consultas excedido",
      });
    }
    count++;
    await loginDocRef.update({checkCount: count});
    if (!docData.user) {
      return res.status(200).json({status: "pending"});
    }
    return res.status(200).json({
      status: "success",
      user: docData.user,
      loginAt: docData.loginAt ? docData.loginAt.toDate() : null,
    });
  } catch (error) {
    console.error(error);
    return res.status(500).json({error: "Erro interno"});
  }
});

exports.performAuth = functions.https.onRequest(app);
