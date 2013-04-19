package com.tradable.exampleApps.useMarketDepth;

import org.springframework.beans.factory.annotation.Autowired;

import com.tradable.api.component.WorkspaceModule;
import com.tradable.api.component.WorkspaceModuleCategory;
import com.tradable.api.component.WorkspaceModuleFactory;
import com.tradable.api.services.marketdata.MarketDepthService;

public class UseMarketDepthFactory implements WorkspaceModuleFactory {

	@Autowired
	MarketDepthService marketDepthService;


	@Override
	public WorkspaceModule createModule() {
		// TODO Auto-generated method stub
		return new UseMarketDepthModule(marketDepthService);
	}

    
	@Override
	public WorkspaceModuleCategory getCategory() {
		// TODO Auto-generated method stub
		return WorkspaceModuleCategory.MISCELLANEOUS;
	}

	@Override
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return "use marketDepth";
	}

	@Override
	public String getFactoryId() {
		// TODO Auto-generated method stub
		return "com.tradable.exampleApps.useMarketDepth";
	}

}
