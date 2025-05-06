const { SmartApp } = require("@smartthings/smartapp");
const FileContextStore = require("@smartthings/file-context-store");
require("dotenv").config();

module.exports = new SmartApp()
  .appId(process.env.APP_ID)
  .clientId(process.env.CLIENT_ID)
  .clientSecret(process.env.CLIENT_SECRET)
  .contextStore(new FileContextStore("data"))
  .redirectUri(`${process.env.SERVER_URL}/smart/oauth/callback`);
