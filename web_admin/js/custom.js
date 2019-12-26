$('#calendar').datepicker({
		});

!function ($) {
    $(document).on("click","ul.nav li.parent > a ", function(){          
        $(this).find('em').toggleClass("fa-minus");      
    }); 
    $(".sidebar span.icon").find('em:first').addClass("fa-plus");
}

(window.jQuery);
	$(window).on('resize', function () {
  if ($(window).width() > 768) $('#sidebar-collapse').collapse('show')
})
$(window).on('resize', function () {
  if ($(window).width() <= 767) $('#sidebar-collapse').collapse('hide')
})

$(document).on('click', '.panel-heading span.clickable', function(e){
    var $this = $(this);
	if(!$this.hasClass('panel-collapsed')) {
		$this.parents('.panel').find('.panel-body').slideUp();
		$this.addClass('panel-collapsed');
		$this.find('em').removeClass('fa-toggle-up').addClass('fa-toggle-down');
	} else {
		$this.parents('.panel').find('.panel-body').slideDown();
		$this.removeClass('panel-collapsed');
		$this.find('em').removeClass('fa-toggle-down').addClass('fa-toggle-up');
	}
})

// setup materialize components
document.addEventListener('DOMContentLoaded', function () {

	var modals = document.querySelectorAll('.modal');
	M.Modal.init(modals);
  
	var items = document.querySelectorAll('.collapsible');
	M.Collapsible.init(items);
	db.collection("news").get().then(function (querySnapshot) {
	  const resDOM = document.querySelector('.news');
	  querySnapshot.forEach(function (doc) {
		const res = doc.data();
		// Create a new JavaScript Date object based on the timestamp
	// multiplied by 1000 so that the argument is in milliseconds, not seconds.
	var date = new Date(res.dateCreated*1000);
	// Hours part from the timestamp
	var hours = date.getHours();
	// Minutes part from the timestamp
	var minutes = "0" + date.getMinutes();
	// Seconds part from the timestamp
	var seconds = "0" + date.getSeconds();

	// Will display time in 10:30:23 format
	var formattedTime = hours + ':' + minutes.substr(-2) + ':' + seconds.substr(-2);
		const htmlData = `
		<span class="chat-img">
		  <img src= ${res.image} />
		</span>
		<div class="chat-body clearfix">
		  <div class="header"><strong class="primary-font">${res.content}</strong> </div>
		  <div <strong class="primary-font">${res.tenquan}</strong> </div>

		  <p>Ngày tạo: ${formattedTime} </p>
		  <br />
		  <p> Address: ${res.address}</p>
		</div>
		`;
	  const node = document.createElement('li');
	  node.setAttribute('class', 'left clearfix');
	  node.setAttribute('style', 'border: 1px; list-style-type:none'); 
	//   node.setAttribute('style', 'boder: 1px');
	  node.innerHTML = htmlData;
	  	resDOM &&resDOM.appendChild(node);
	  });
	}).finally(() => {
	  document.querySelector('#loading-data').remove();
	});
});
// 	db.collection("user").get().then(function (querySnapshot) {
// 	  const resDOM = document.querySelector('.user');
// 	  querySnapshot.forEach(function (doc) {
// 		const res = doc.data();
// 		let htmlData = ''
// 		if(res.provice){
// 		  if(res.phone){
// 			htmlData = `
// 			<span class="chat-img pull-left">
// 			  <img src= ${res.image} alt="User Avatar" width='100' height= />
// 			</span>
// 			<div class="chat-body clearfix">
// 			  <div class="header"><strong class="primary-font">${res.name}</strong> <small
// 			  class="text-muted">${res.email}</small></div>
// 			  <p>  Phone:${res.phone} <br />  Street: ${res.street +'-'+ res.district +'-'+ res.provice||''}</p>
// 			</div>
// 			`;
// 		  }
// 		  else{
// 			htmlData = `
// 			<span class="chat-img pull-left">
// 			  <img src= ${res.image} alt="User Avatar" width='100' height= />
// 			</span>
// 			<div class="chat-body clearfix">
// 			  <div class="header"><strong class="primary-font">${res.name}</strong> <small
// 			  class="text-muted">${res.email}</small></div>
// 			  <p>Street: ${res.street +'-'+ res.district +'-'+ res.provice||''}</p>
// 			</div>
// 			`;
// 		  }
// 		}
// 		else {
  
  
// 		  if(res.phone){
// 			htmlData = `
// 			<span class="chat-img pull-left">
// 			  <img src= ${res.image} alt="User Avatar" width='100' height= />
// 			</span>
// 			<div class="chat-body clearfix">
// 			  <div class="header"><strong class="primary-font">${res.name}</strong> <small
// 			  class="text-muted">${res.email}</small></div>
			 
// 			  <p Phone:${res.phone} <br /> Street: ${res.street +'-'+ res.district}</p>
// 			</div>
// 			`;
// 		  }
// 		  else{
// 			htmlData = `
// 			<span class="chat-img pull-left">
// 			  <img src= ${res.image} alt="User Avatar" width='100' height= />
// 			</span>
// 			<div class="chat-body clearfix">
// 			  <div class="header"><strong class="primary-font">${res.name}</strong> <small
// 			  class="text-muted">${res.email}</small></div>
// 			  <p>Street: ${res.street +'-'+ res.district}</p>
// 			</div>
// 			`;
  
// 		  }
		 
// 		}
	   
// 	  const node = document.createElement('li');
// 	  node.setAttribute('class', 'right clearfix');
// 	  node.innerHTML = htmlData;
// 		resDOM.appendChild(node);
// 	  });
// 	}).finally(() => {
// 	  document.querySelector('#loading-data').remove();
// 	});
  
//   });