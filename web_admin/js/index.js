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
        <button onclick=deleteBtnRes(${id}) type="button" class="btn btn-primary" style="font-size : 10px; float:right; " id=${id} >Delete</button>
        <p> Phone: ${res.phone} <br />Street: ${res.street + res.district + res.type}</p>
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
    const resDOM_shippers = document.querySelector('.shippers');
    const resDOM_owner = document.querySelector('.restaurant-ownwers');


    let res;
    let image;
    querySnapshot.forEach(function (doc) {
      res = doc.data();
      if(res.image === '') {
        console.log("Hha");
        image = 'avatar.jpeg';
      }
      else image = res.image
     
      id = doc.id
      console.log(res)
      let htmlData = ''
      if (res.province) {
        htmlData = `
          <span class="chat-img pull-left" style="margin:8px">
            <img class="img-circle"  src= ${image}  width='100' height='100' />
          </span>
          <div class="chat-body clearfix">
            <div class="header"><strong class="primary-font">${res.name}</strong> <small
            class="text-muted">${res.email}</small></div>
            <button onClick="deleteBtn('${this.id}','${res.type}')" type="button" class="btn btn-primary" style="font-size : 10px; float:right; " id=${id} >Delete</button>
            <p>Phone: ${res.phone} <br /> Street: ${res.street + '-' + res.district + '-' + res.province}<br /></p>
          </div>
          `;

      }
      else {
        htmlData = `
          <span class="chat-img pull-left mr-5">
            <img src= ${image} alt="User Avatar" width='100' height=100 style="margin:8px" />
          </span>
          <div class="chat-body clearfix">
            <div class="header"><strong class="primary-font">${res.name}</strong> <small
            class="text-muted">${res.email}</small></div>
            <button onclick="deleteBtn('${this.id}','${res.type}')" type="button" class="btn btn-primary" style="font-size : 10px; float:right; " id=${id} >Delete</button>
            <p>Phone: ${res.phone} <br /> Street: ${res.street + '-' + res.district}<br /></p>
          </div>
          <br />
          `;

      }
      const node = document.createElement('li');
      node.setAttribute('class', 'left clearfix');
      node.setAttribute('style', ' list-style-type:none');
      node.setAttribute('id', id);
      node.innerHTML = htmlData;
      if (res.type ==='1') resDOM.appendChild(node);
      else if(res.type ==='2') resDOM_shippers.appendChild(node);
      else resDOM_owner.appendChild(node);
      
    });
  }).finally(() => {
    document.querySelector('#loading-data').remove();
  });

});

const deleteBtn = (id, type) => {
  var getConfirm = confirm('Do you want delete ???');
  if (getConfirm) {
    if (type == '3') {
      console.log('id', id);
      
      db.collection("restaurants").where("user_id", "==", id)
        .get()
        .then(     
          (querySnapshot) => {
            console.log(querySnapshot.empty);
            
            querySnapshot.forEach(
               (doc) => {
                document.getElementById(doc.id).remove();
                doc.delete();
              }
            );
          }
        )
        .catch(function (error) {
          console.log("Error getting documents: ", error);
        });
  
    }
    console.log("type = "+type)
    db.collection("user").doc(id).delete().then(function () {
      console.log("Document successfully deleted!");
      document.getElementById(id).remove();
    }).catch(function (error) {
      console.error("Error removing document: ", error);
    });
  }
}

const deleteBtnRes = (id ) => {
  db.collection("restaurants").doc(id).get().then(function (querySnapshot) {
    document.getElementById(id).remove();
    querySnapshot.forEach(function (doc) {
      res = doc.data();
      document.getElementById(res.user_id).remove();
      db.collection("user").doc(res.user_id).delete()
    });
  });

  db.collection("restaurants").doc(id).delete();
}

// lấy thẻ input
// định nghĩa hàm xử lý myFunction
const myFunction = () => {
  var input = document.getElementById("btn-input");
  console.log("input=",input)
  var filter, ul, li, a, i;
  // lấy giá trị người dùng nhập
  filter = input.value.toUpperCase();
  ul = document.getElementsByClassName("restaurants")[0];
  li = ul.getElementsByTagName("li");
  // Nếu filter không có giá trị thị ẩn phần kết quare\
  if (!filter.length) {
    for (i = 0; i < li.length; i++) {
      li[i].style.display = "";
    }
    ul.style.display = "block";
  } else {
    // lặp qua tất cả các thẻ li chứa kết quả
    for (i = 0; i < li.length; i++) {
      // lấy thẻ a trong các thẻ li
      a = li[i].getElementsByTagName("div")[0];
      // kiểm tra giá trị nhập có tôn tại trong nội dung thẻ a
      if (a.innerHTML.toUpperCase().indexOf(filter) > -1) {
        //nếu có hiển thị phàn tử ul và các thẻ li đó
        ul.style.display = "block";
        li[i].style.display = "";
      } else {
        // nếu không ẩn các thẻ li
        li[i].style.display = "none";

      }
    }
  }
}
const myFunction_1 = () => {
  var input = document.getElementById("btn-input_1");
  console.log("input=",input)
  var filter, ul, li, a, i;
  // lấy giá trị người dùng nhập
  filter = input.value.toUpperCase();
  ul = document.getElementsByClassName("user")[0];
  li = ul.getElementsByTagName("li");
  // Nếu filter không có giá trị thị ẩn phần kết quare\
  console.log(filter);
  if (!filter.length) {
    console.log("HAHHAHAH");
    for (i = 0; i < li.length; i++) {
      li[i].style.display = "";
    }
    ul.style.display = "block";
  } else {
    // lặp qua tất cả các thẻ li chứa kết quả
    for (i = 0; i < li.length; i++) {
      // lấy thẻ a trong các thẻ li
      a = li[i].getElementsByTagName("div")[0];
      // kiểm tra giá trị nhập có tôn tại trong nội dung thẻ a
      if (a.innerHTML.toUpperCase().indexOf(filter) > -1) {
        //nếu có hiển thị phàn tử ul và các thẻ li đó
        ul.style.display = "block";
        li[i].style.display = "";
      } else {
        // nếu không ẩn các thẻ li
        li[i].style.display = "none";

      }
    }
  }
}
const myFunction_2 = () => {
  var input = document.getElementById("btn-input_2");
  console.log("input=",input)
  var filter, ul, li, a, i;
  // lấy giá trị người dùng nhập
  filter = input.value.toUpperCase();
  ul = document.getElementsByClassName("restaurant-ownwers")[0];
  li = ul.getElementsByTagName("li");
  // Nếu filter không có giá trị thị ẩn phần kết quare\
  if (!filter.length) {
    for (i = 0; i < li.length; i++) {
      li[i].style.display = "";
    }
    ul.style.display = "block";
  } else {
    // lặp qua tất cả các thẻ li chứa kết quả
    for (i = 0; i < li.length; i++) {
      // lấy thẻ a trong các thẻ li
      a = li[i].getElementsByTagName("div")[0];
      // kiểm tra giá trị nhập có tôn tại trong nội dung thẻ a
      if (a.innerHTML.toUpperCase().indexOf(filter) > -1) {
        //nếu có hiển thị phàn tử ul và các thẻ li đó
        ul.style.display = "block";
        li[i].style.display = "";
      } else {
        // nếu không ẩn các thẻ li
        li[i].style.display = "none";

      }
    }
  }
}
const myFunction_3 = () => {
  var input = document.getElementById("btn-input_3");
  console.log("input=",input)
  var filter, ul, li, a, i;
  // lấy giá trị người dùng nhập
  filter = input.value.toUpperCase();
  ul = document.getElementsByClassName("shippers")[0];
  li = ul.getElementsByTagName("li");
  // Nếu filter không có giá trị thị ẩn phần kết quare\
  if (!filter.length) {
    for (i = 0; i < li.length; i++) {
      li[i].style.display = "";
    }
    ul.style.display = "block";
  } else {
    // lặp qua tất cả các thẻ li chứa kết quả
    for (i = 0; i < li.length; i++) {
      // lấy thẻ a trong các thẻ li
      a = li[i].getElementsByTagName("div")[0];
      // kiểm tra giá trị nhập có tôn tại trong nội dung thẻ a
      if (a.innerHTML.toUpperCase().indexOf(filter) > -1) {
        //nếu có hiển thị phàn tử ul và các thẻ li đó
        ul.style.display = "block";
        li[i].style.display = "";
      } else {
        // nếu không ẩn các thẻ li
        li[i].style.display = "none";

      }
    }
  }
}

//gán sự kiện cho thẻ input
// input.addEventListener("keyup", myFunction);
