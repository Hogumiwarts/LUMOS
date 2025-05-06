exports.handleUninstall = async (req, res, smartApp) => {
    await smartApp.handleHttpCallback(req, res);
  };
  