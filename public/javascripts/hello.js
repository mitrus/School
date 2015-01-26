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