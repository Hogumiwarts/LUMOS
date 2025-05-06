const path = require('path');
const swaggerJSDoc = require('swagger-jsdoc');
require('dotenv').config();

const options = {
  definition: {
    openapi: '3.0.0',
    info: {
      title: '[LUMOS] REST API',
      version: '1.0.0',
      description: 'SSAFY 자율 프로젝트 LUMOS API 문서',
    },
    servers: [
      {
        url: '/',
      },
    ],
  },
  apis: [path.join(__dirname, '../app/routes/**/*.js')],
};

const swaggerSpec = swaggerJSDoc(options);

// default 주입 유틸
function injectDefaultValue(pathKey, method, paramName, defaultValue) {
  const op = swaggerSpec.paths?.[pathKey]?.[method];
  if (!op || !Array.isArray(op.parameters)) return;

  const param = op.parameters.find(p => p.name === paramName);
  if (param && param.schema) {
    param.schema.default = defaultValue;
  }
}

// default 값 주입
injectDefaultValue('/devices', 'get', 'installedappid', process.env.DEFAULT_INSTALLED_APP_ID);
injectDefaultValue('/devices/{deviceId}/status', 'get', 'installedappid', process.env.DEFAULT_INSTALLED_APP_ID);
injectDefaultValue('/devices/{deviceId}/status', 'get', 'deviceId', process.env.DEFAULT_DEVICE_ID);
injectDefaultValue('/devices/{deviceId}/command', 'post', 'installedappid', process.env.DEFAULT_INSTALLED_APP_ID);
injectDefaultValue('/devices/{deviceId}/command', 'post', 'deviceId', process.env.DEFAULT_DEVICE_ID);

module.exports = swaggerSpec;
