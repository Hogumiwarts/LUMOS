exports.handleInstall = async (req, res, smartApp) => {
    await smartApp.handleHttpCallback(req, res);
  };
  