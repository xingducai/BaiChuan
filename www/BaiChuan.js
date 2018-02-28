var exec = require('cordova/exec');

exports.coolMethod = function (arg0, success, error) {
    exec(success, error, 'BaiChuan', 'coolMethod', [arg0]);
};


exports.detailPage = function (arg0, success, error) {
    exec(success, error, 'BaiChuan', 'detailPage', [arg0]);
};
exports.login = function (arg0, success, error) {
    exec(success, error, 'BaiChuan', 'login', [arg0]);
};
exports.pageUrl = function (arg0, success, error) {
    exec(success, error, 'BaiChuan', 'pageUrl', [arg0]);
};
exports.pageOrder = function (arg0, success, error) {
    exec(success, error, 'BaiChuan', 'pageOrder', [arg0]);
};
