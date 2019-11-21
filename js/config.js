(function(){
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
  // Initialize Firebase
  firebase.initializeApp(firebaseConfig);
//   firebase.analytics();

const txtEmail = document.getElementById('txtEmail');
const txtPassword = document.getElementById('txtPassword');
const btnLogin = document.getElementById('btnLogin');

btnLogin.addEventListener('click', e =>{
    const email = txtEmail.value;
    const Password = txtPassword.value;
    const auth = firebase.auth();
    const promise = auth.signInWithEmailAndPassword(email, Password);
    console.log(email);
    console.log(Password);
    promise.catch(e => console.log(e.message))
});



// btnLogin.addEventListener('click',e=>{
//     const email = txtEmail;
//     const Password = t   xtPassword;
//     const auth = firebase.auth();
//     const promise = auth.signInWithEmailAndPassword(email, Password);

//     promise.catch(e => console.log(e.message))
// })

firebase.auth().onAuthStateChanged(function(firebaseUse){
    if(firebaseUser){
        consolele.log(firebaseUser);
        window.location="index.html";
    } else{
        console.log('not logged in');
        // window.location.reload(); 
    }
});
}());