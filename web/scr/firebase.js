import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";

const firebaseConfig = {
  apiKey: "AIzaSyDqJ6QCVln0vbltsGHi8BOaS8vGf_6PSW4",
  authDomain: "super-6a237.firebaseapp.com",
  projectId: "super-6a237",
  storageBucket: "super-6a237.firebasestorage.app",
  messagingSenderId: "1004752914839",
  appId: "1:1004752914839:web:93f0f1ed524e37cc4e1cd2",
  measurementId: "G-CB7032GWTK"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);