const buildSmartThingsAuthUrl = ({ clientId, redirectUri, scopes = [] }) => {
    const baseUrl = "https://api.smartthings.com/oauth/authorize";
  
    const query = new URLSearchParams({
      client_id: clientId,
      response_type: "code",
      scope: scopes.join(" "), // 배열 → 문자열 변환
      redirect_uri: redirectUri
    });
  
    return `${baseUrl}?${query.toString()}`;
  };
  
  module.exports = { buildSmartThingsAuthUrl };
  