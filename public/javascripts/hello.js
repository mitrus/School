function alertMSG() {
  $.ajax({
    url: 'test',
    dataType: 'json',
    success: function(response) {
      console.log(response);
    }
  });
}

function toggleRegOption() {
  console.log('dsd')
  if ($('#studentOption').is(':checked')) {
    $('#regTeacherDiv :input').attr('disabled', true);
    $('#regStudentDiv :input').removeAttr('disabled');
//    $('#underSecretKey :input').attr('disabled', true);
  } else {
    $('#regStudentDiv :input').attr('disabled', true);
    $('#regTeacherDiv :input').removeAttr('disabled');
  }
}

$(document).mouseup(function (e)
{
  var container = $("#add_form");

  if (!container.is(e.target) // if the target of the click isn't the container...
    && container.has(e.target).length === 0) // ... nor a descendant of the container
  {
    container.hide(100);
  }
});

//function al() { alert($('#testId')[0].options[$('#testId')[0].selectedIndex].value); }

function addForm() {
  var form = $("#form_number_input").val()
  if (form.length > 0) {
    $.ajax({
      url: "/api/addForm/" + form,
      success: function (result) {
        $("#forms_list").append(new Option(form, form))
      }
    });
  }
}

function addSubject() {
  var form = $("#form_number_input").val()
  if (form.length > 0) {
    $.ajax({
      url: "/api/addSubject/" + form,
      success: function (result) {
        $("#forms_list").append(new Option(form, form))
      }
    });
  }
}