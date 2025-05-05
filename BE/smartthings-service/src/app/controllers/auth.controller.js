const { buildSmartThingsAuthUrl } = require('../utils/buildAuthUrl');
const smartApp = require('../../config/smartapp');

// 인증 URL 만들어서 반환
exports.getAuthUrl = (req, res) => {
  const clientId = process.env.CLIENT_ID;
  const redirectUri = `${process.env.SERVER_URL}/smart/oauth/callback`;

  const scopes = [
    "r:locations:*",
    "r:devices:*",
    "x:devices:*"
  ];

  const url = buildSmartThingsAuthUrl({ clientId, redirectUri, scopes });

  res.send({ url });
};

// 인증 완료 후 콜백 처리 (AccessToken 발급)
exports.oauthCallback = async (req, res) => {
    try {
      console.log("✅ [DEBUG] Query Params:", req.query); // 추가
      const ctx = await smartApp.handleOAuthCallback(req);
      console.log("✅ OAuth 인증 완료! InstalledAppId:", ctx.installedAppId);

      const installedAppId = ctx.installedAppId;
      const authToken = ctx.authToken;

      // 안드로이드 앱용 딥링크로 redirect
      const redirectUrl = `smartthingslogin://oauth-callback?installedAppId=${installedAppId}&authToken=${authToken}`;
      
      res.redirect(redirectUrl);
    } catch (err) {
      console.error("❌ OAuth 실패:", err.response?.data || err.message);
      res.status(500).send("❌ OAuth 실패: " + (err.response?.data?.error_description || err.message));
    }
  };