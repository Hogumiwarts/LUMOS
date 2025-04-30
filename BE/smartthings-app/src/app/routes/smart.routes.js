const express = require('express');
const router = express.Router();
const smartApp = require('../../config/smartapp');

// lifecycle 핸들러 import
const { handleConfirmation } = require('../lifecycle/confirmation.handler');
const { handleConfiguration } = require('../lifecycle/configuration.handler');
const { handleInstall } = require('../lifecycle/install.handler');
const { handleUpdate } = require('../lifecycle/update.handler');
// const { handleEvent } = require('../lifecycle/event.handler');
const { handleUninstall } = require('../lifecycle/uninstall.handler');

// POST /smart
router.post('/', async (req, res) => {
  console.log("📥 POST /smart 요청 도착!");
  console.log(JSON.stringify(req.body, null, 2));

  const lifecycle = req.body.lifecycle;

  switch (lifecycle) {
    case 'CONFIRMATION':
      await handleConfirmation(req, res);
      break;
    case 'CONFIGURATION':
      await handleConfiguration(req, res);
      break;
    case 'INSTALL':
      await handleInstall(req, res, smartApp);
      break;
    case 'UPDATE':
      await handleUpdate(req, res, smartApp);
      break;
    case 'EVENT':
      await handleEvent(req, res, smartApp);
      break;
    case 'UNINSTALL':
      await handleUninstall(req, res, smartApp);
      break;
    default:
      res.status(400).send(`❌ Unknown lifecycle: ${lifecycle}`);
  }
});

module.exports = router;
