var pageConfiguration = {
  needAuth: false
};

$(function() {
  if (pageConfiguration.needAuth) {
    if (!isLocalAuth()) {
      LocationUtil.goto('/user/login');
      return;
    }
  }

  renderHead();
  renderFoot();

  if (onload) {
    onload();
  }

  function renderHead() {
    var content = '<div class="innerBox"><ul class="top-head">';
    content += '<li><a th:href="/">首页</a></li>';
    if (!isLocalAuth()) {
      content += '<li><a th:href="@{/user/login}">登录</a></li>';
    } else {
      content += '<li><a th:href="@{/user/profile}">个人中心</a></li>';
      content += '<li><a th:href="@{/user/logout}">注销</a></li>';
    }
    content += '</ul></div>';
    $('#head').html(content);
  }

  function renderFoot() {
    var content = '<div class="innerBox">';
    content += '</div>';
    $('#foot').html(content);
  }

  function isLocalAuth() {
    var token = getRequestHeaders()['auth-token'];
    return token && token != null && token.length > 10;
  }

});

function renderMenu(arr, selector, current) {
  var content = '';
  $.each(arr, function(index, item) {
    if (item.state !== 0) {
      if (current === item.id) {
        content += '<li class="current"><a href="javascript:void(0);">' + item.title +'</a></li>';
      } else {
        content += '<li ><a href="' + item.url + '">' + item.title +'</a></li>';
      }
    }
  });

  $(selector).html(content);
}

function getRequestHeaders() {
  return {
    'auth-token': StorageUtil.get(GlobalConsts.authTokenKey)
  }
}
