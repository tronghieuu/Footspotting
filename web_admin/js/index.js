const guideList = document.querySelector('.guides');
const loggedOutLinks = document.querySelectorAll('.logged-out');
const loggedInLinks = document.querySelectorAll('.logged-in');
const accountDetails = document.querySelector('.account-details');
const adminItems = document.querySelectorAll('.admin');

const setupUI = (user) => {
  if (user) {
    if (user.admin) {
      adminItems.forEach(item => item.style.display = 'block');
    }
    // account info
    db.collection('users').doc(user.uid).get().then(doc => {
      const html = `
        <div>Logged in as ${user.email}</div>
        <div class="pink-text">${user.admin ? 'Admin' : ''}</div>
      `;
      accountDetails.innerHTML = html;
    });
    // toggle user UI elements
    loggedInLinks.forEach(item => item.style.display = 'block');
    loggedOutLinks.forEach(item => item.style.display = 'none');
  } else {
    // clear account info
    accountDetails.innerHTML = '';
    // toggle user elements
    adminItems.forEach(item => item.style.display = 'none');
    loggedInLinks.forEach(item => item.style.display = 'none');
    loggedOutLinks.forEach(item => item.style.display = 'block');
  }
};

// setup guides
const setupGuides = (data) => {

  if (data.length) {
    let html = '';
    data.forEach(doc => {
      const guide = doc.data();
      const li = `
        <li>
          <div class="collapsible-header grey lighten-4"> ${guide.title} </div>
          <div class="collapsible-body white"> ${guide.content} </div>
        </li>
      `;
      html += li;
    });
    guideList.innerHTML = html
  } else {
    guideList.innerHTML = '<h5 class="center-align">Wellcome to website for admin</h5>';
  }


};

// setup materialize components
document.addEventListener('DOMContentLoaded', function () {

  var modals = document.querySelectorAll('.modal');
  M.Modal.init(modals);

  var items = document.querySelectorAll('.collapsible');
  M.Collapsible.init(items);
  db.collection("restaurants").get().then(function (querySnapshot) {
    const resDOM = document.querySelector('.restaurants');
    querySnapshot.forEach(function (doc) {
      const res = doc.data();
      const id = doc.id
      const htmlData = `
      <span class="chat-img pull-left">
        <img src= ${res.image} alt="User Avatar" class="img-circle" />
      </span>
      <div class="chat-body clearfix">
        <div class="header"><strong class="primary-font">${res.name}</strong> <small
        class="text-muted">${res.closing_time}</small></div>
        <button onclick=deleteBtnRes(this.id) type="button" class="btn btn-primary" style="font-size : 10px; float:right; " id=${id} >Delete</button>
        <p> Phone: ${res.phone} <br />Stre  et: ${res.street + res.district + res.type}</p>
      </div>
      `;
    const node = document.createElement('li');
    node.setAttribute('class', 'left clearfix');
    node.setAttribute('id', id);
    node.innerHTML = htmlData;
      resDOM.appendChild(node);
    });
  }).finally(() => {
    document.querySelector('#loading-data').remove();
  });

  db.collection("user").get().then(function (querySnapshot) {
    const resDOM = document.querySelector('.user');
    let res;
    querySnapshot.forEach(function (doc) {
      res = doc.data();
      id = doc.id
      console.log(res)
      let htmlData = ''
      if(res.provice){
          htmlData = `
          <span class="chat-img pull-left">
            <img class="img-circle"  src= ${res.image}  width='100' height='100' />
          </span>
          <div class="chat-body clearfix">
            <div class="header"><strong class="primary-font">${res.name}</strong> <small
            class="text-muted">${id}</small></div>
            <button onclick=deleteBtn(this.id, res.type) type="button" class="btn btn-primary" style="font-size : 10px; float:right; " id=${id} >Delete</button>
            <p>Phone: ${res.phone} <br /> Street: ${res.street +'-'+ res.district +'-'+ res.provice}</p>
          </div>
          `;
      
      }
      else {
          htmlData = `
          <span class="chat-img pull-left">
            <img src= ${res.image} alt="User Avatar" width='100' height=100 />
          </span>
          <div class="chat-body clearfix">
            <div class="header"><strong class="primary-font">${res.name}</strong> <small
            class="text-muted">${res.email}</small></div>
            <button onclick=deleteBtn(this.id) type="button" class="btn btn-primary" style="font-size : 10px; float:right; " id=${id} >Delete</button>
            <p>Phone: ${res.phone} <br /> Street: ${res.street +'-'+ res.district}</p>
          </div>
          <br />
          `;      
       
      }
    const node = document.createElement('li');
    node.setAttribute('class', 'left clearfix');
    node.setAttribute('style', ' list-style-type:none'); 
    node.setAttribute('id', id);
    node.innerHTML = htmlData;
      resDOM.appendChild(node);
    });
  }).finally(() => {
    document.querySelector('#loading-data').remove();
  });

});



const deleteBtn = (id,type =1) =>{
  if(type == 3){
    db.collection(".restaurants").where("user_id","==", id)
    .get()
    .then(function(querySnapshot) {
        querySnapshot.forEach(function(doc) {
          console.log(doc);
            doc.delete();
           
        });
    })
    .catch(function(error) {
        console.log("Error getting documents: ", error);
    });

  }
 
  db.collection("user").doc(id).delete().then(function() {
  console.log("Document successfully deleted!");
  document.getElementById(id).remove();
}).catch(function(error) {
    console.error("Error removing document: ", error);
});
}

const deleteBtnRes = (id,) =>{
  db.collection("restaurants").doc(id).get().then(function (querySnapshot) {
    document.getElementById(id).remove();

    querySnapshot.forEach(function (doc) {
    res =doc.data();
    document.getElementById(res.user_id).remove();
    db.collection("user").doc(res.user_id).delete()
    });
  });

  db.collection("restaurants").doc(id).delete();
}
