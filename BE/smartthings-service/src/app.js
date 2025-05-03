const express = require('express');
require('dotenv').config({ path: '../.env' });

const swaggerSpec = require('./config/swagger');
const swaggerSpecDev = require('./config/swagger.dev');
const swaggerUi = require('swagger-ui-express');

const app = express();
const smartRoutes = require('./app/routes/smart.routes');
const authRoutes = require('./app/routes/auth.routes');
const deviceRoutes = require('./app/routes/device.routes');

// JSON 응답만 따로 처리 (api-docs 경로)
app.get('/v3/api-docs', (req, res) => {
  res.send(swaggerSpec);
});

// 개발용 Swagger UI
app.use('/swagger-ui', swaggerUi.serve, swaggerUi.setup(swaggerSpecDev));

app.use(express.json());

// OAuth 관련 API
app.use('/smart/oauth', authRoutes);

// 디바이스 관련 API
app.use('/smart/devices', deviceRoutes);

// SmartApp 관련 API
app.use('/smart', smartRoutes);

const port = process.env.PORT || 3000;
app.listen(port, () => {
  console.log(`✅ Server running at http://localhost:${port}`);
  console.log('✅ Swagger docs at http://localhost:3000/swagger-ui');
});
