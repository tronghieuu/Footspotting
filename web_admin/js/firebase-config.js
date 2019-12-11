const firebaseConfig = {
    apiKey: "AIzaSyA6PG2O-YPFDQwBgVhXijnIIAs9SO3vtY4",
    authDomain: "foodspotting-11369.firebaseapp.com",
    databaseURL: "https://foodspotting-11369.firebaseio.com",
    projectId: "foodspotting-11369",
    storageBucket: "foodspotting-11369.appspot.com",
    messagingSenderId: "863159936668",
    appId: "1:863159936668:web:6b002c336e5ed6c4daef59",
    measurementId: "G-6QJ312FVNV"
  };

  firebase.initializeApp(firebaseConfig);

  const db = firebase.firestore();
  db.settings({ timestampsInSnapshots: true });