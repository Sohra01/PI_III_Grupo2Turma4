import { initializeApp } from "https://www.gstatic.com/firebasejs/9.22.2/firebase-app.js";
import { getFunctions, httpsCallable } from "https://www.gstatic.com/firebasejs/9.22.2/firebase-functions.js";

const firebaseConfig = {
    apiKey: "AIzaSyDqJ6QCVln0vbltsGHi8BOaS8vGf_6PSW4",
    authDomain: "super-6a237.firebaseapp.com",
    projectId: "super-6a237",
    storageBucket: "super-6a237.firebasestorage.app",
    messagingSenderId: "1004752914839",
    appId: "1:1004752914839:web:93f0f1ed524e37cc4e1cd2",
    measurementId: "G-CB7032GWTK"
};

const app = initializeApp(firebaseConfig);
const functions = getFunctions(app);
const userUID = sessionStorage.getItem("userUID");

let countdownInterval = null;
let currentLoginToken = null;
let remainingSeconds = 60;

if (userUID) {
    window.location.href = "home.html";
}


async function fetchQRCode() {
  try {
    const response = await fetch("https://us-central1-super-6a237.cloudfunctions.net/performAuth", {
    method: "POST",
    headers: {
        "Content-Type": "application/json",
    },
    body: JSON.stringify({
        apiKey: "U0VjcmV0QXBpS2V5X1ZhbHVlX0dlbmVyYXRlZF9Gb3JfU3VwZXJJRF9XZWIyMDI1X1dpdGhfTG90c09mUmFuZG9tQnl0ZXMhISEhISEhISEhISEhISEhISEhISEhISEhISE=",
        siteUrl: "www.seusiteparceiro.com",
    }),
    });

    const result = await response.json();

    if (!response.ok) {
      throw new Error(result.error || "Erro desconhecido.");
    }

     // Salva o token para polling
    currentLoginToken = result.loginToken;
    remainingSeconds = 60;

    // Insere QR Code e mostra o modal
    document.getElementById("qr-code-image").innerHTML =
      `<img src="${result.qrCodeBase64}" alt="QR Code" style="width:100%; max-width:300px;" />`;
    document.getElementById("qr-modal").style.display = "flex";


    updateTimerUI();

    countdownInterval = setInterval(() => {
      remainingSeconds--;
      updateTimerUI();
      if (remainingSeconds <= 0) {
        clearInterval(countdownInterval);
        alert("Tempo esgotado. Por favor, gere um novo QR Code.");
        closeModal();
      }
    }, 1000);

  } catch (e) {
    alert("Erro ao gerar QR Code: " + e.message);
  }
}

async function checkLoginStatus() {
  console.log("checkLoginStatus chamado");
  if (!currentLoginToken) return;

  const response = await fetch("https://us-central1-super-6a237.cloudfunctions.net/performAuth/getLoginStatus", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ loginToken: currentLoginToken }),
  });

  const result = await response.json();

  if (result.status === "success") {
    clearInterval(countdownInterval);
    sessionStorage.setItem("userUID", result.user);
    closeModal();
    window.location.href = "home.html";
  } else if (result.error === "loginToken expirado" || result.error === "Número máximo de consultas excedido") {
    clearInterval(countdownInterval);
    alert("QR Code expirado ou inválido.");
    closeModal();
  }
}

function updateTimerUI() {
  const timerElement = document.getElementById("qr-timer");
  timerElement.textContent = `O QR Code expira em ${remainingSeconds}s`;
}

function closeModal() {
  document.getElementById("qr-modal").style.display = "none";
  document.getElementById("qr-code-image").innerHTML = "";
  document.getElementById("qr-timer").textContent = "";
  currentLoginToken = null;
  clearInterval(countdownInterval);
}

// Fechar modal ao clicar no X
document.getElementById("qr-modal-close").addEventListener("click", () => {
  document.getElementById("qr-modal").style.display = "none";
});

document.getElementById("btn-login").addEventListener("click", async () => {
  await fetchQRCode();

  setTimeout(() => checkLoginStatus(), 20000); 
  setTimeout(() => checkLoginStatus(), 40000); 
  setTimeout(() => checkLoginStatus(), 60000);
});