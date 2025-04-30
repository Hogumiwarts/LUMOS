exports.handleConfiguration = async (req, res) => {
    const phase = req.body.configurationData.phase;
  
    if (phase === 'INITIALIZE') {
      res.json({
        configurationData: {
          initialize: {
            name: "My SmartApp",
            description: "Control your devices",
            id: "app",
            firstPageId: "1"
          }
        }
      });
    } else if (phase === 'PAGE') {
      res.json({
        configurationData: {
          page: {
            pageId: "1",
            name: "Device Selection",
            nextPageId: null,
            previousPageId: null,
            complete: true,
            sections: [
              {
                name: "Choose Devices",
                settings: [
                  {
                    id: "selectedSwitches",
                    name: "Select switches",
                    description: "Tap to choose",
                    type: "DEVICE",
                    required: true,
                    multiple: true,
                    capabilities: ["switch"], // 스위치 디바이스만 필터
                    permissions: ["r", "x"]     // 읽기(r), 제어(x) 권한
                  }
                ]
              }
            ]
          }
        }
      });
    } else {
      res.status(400).send('Invalid phase');
    }
  };
  