// See http://parseplatform.github.io/docs/js/guide/
// https://parseplatform.github.io/docs/js/guide/#query-constraints
Parse.Cloud.define('pushToChannel', function(request, response) {

  var params = request.params;

  var channel = params.channel;

  if (!channel) {
     response.error('must provide a channel');
  }

  var customData = params.customData;

  var query = new Parse.Query(Parse.Installation);
  var oneWeekAgo = new Date(Date.now() -  (7 * 24 * 60 * 60 * 1000));

  query.equalTo('channels', channel);
  query.equalTo('deviceType', 'android');
  query.greaterThanOrEqualTo('updatedAt', oneWeekAgo);

  var payload = {};

  if (customData) {
      payload = JSON.parse(customData);
  }

  // Note that useMasterKey is necessary for Push notifications to succeed.

  Parse.Push.send({
  where: query,
  // Parse.Push requires a dictionary, not a string.
  data: {"customData": customData},
  }, { success: function() {
     console.log("#### PUSH OK");
  }, error: function(error) {
     console.log("#### PUSH ERROR" + error.message);
  }, useMasterKey: true});

  response.success('success');
});