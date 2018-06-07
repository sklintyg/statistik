var path = require('path');
var eval = require('eval');
var file = path.join(process.cwd(), '../web/src/main/webapp/app/messages.js');
var texter = require('fs').readFileSync(file, 'utf8');

var res = eval(texter + ';exports.text = stMessages;');


module.exports = res.text.sv;
