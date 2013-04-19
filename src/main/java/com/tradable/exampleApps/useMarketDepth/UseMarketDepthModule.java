package com.tradable.exampleApps.useMarketDepth;


import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;

import com.tradable.api.component.WorkspaceModule;
import com.tradable.api.component.WorkspaceModuleProperties;
import com.tradable.api.component.state.PersistedStateHolder;
import com.tradable.api.services.marketdata.MarketDepthEvent;
import com.tradable.api.services.marketdata.MarketDepthListener;
import com.tradable.api.services.marketdata.MarketDepthService;
import com.tradable.api.services.marketdata.MarketDepthSubscription;
import com.tradable.api.services.marketdata.Quote;

import javax.swing.text.BadLocationException;

public class UseMarketDepthModule extends JPanel implements WorkspaceModule, MarketDepthListener{
	

	//change this number to something more sensible
	private static final long serialVersionUID = 1L; 
	private MarketDepthService marketDepthService;
	private MarketDepthSubscription marketDepthSubscription;
	private JScrollPane scrollPane;
	private JTextPane textPane;
	
	public UseMarketDepthModule(MarketDepthService marketDepthService) {
		setLayout(null);
		setSize(400, 400);
		setModuleTitle("Use MarketDepth");
		putClientProperty(WorkspaceModuleProperties.COMPONENT_RESIZE_ENABLED, false);
		
		//creates an empty Jlabel that will contain the accountID upon 
		//catching an event.
		
		scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(0, 0, 400, 400);
		add(scrollPane);
		
		textPane = new JTextPane();
		textPane.setEditable(false);
		scrollPane.setViewportView(textPane);
		
		this.marketDepthService = marketDepthService;	
		this.marketDepthSubscription = this.marketDepthService.createMarketDepthSubscription();
		this.marketDepthSubscription.addSymbol("EURUSD");
		this.marketDepthSubscription.setListener(this);
			
	}

	@Override
	public JComponent getVisualComponent() {
		return this;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		marketDepthSubscription.destroy();
		
	}

	@Override
	public PersistedStateHolder getPersistedState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void loadPersistedState(PersistedStateHolder state) {
		// TODO Auto-generated method stub
		
	}
	

    protected void setModuleTitle(String title) {
        putClientProperty(WorkspaceModuleProperties.COMPONENT_TITLE, title);
    }
    
    
	@Override
	public void marketDepthUpdated(MarketDepthEvent event) {
		
        String symbol = event.getSymbol();
        for (Quote bid : event.getSource().getBids(symbol)) {
        	
    		try {
				textPane.getDocument().insertString(textPane.getDocument().getLength() ,
						"order of size: " + String.valueOf(bid.getSize()) + 
								" moved the bid price to: " + String.valueOf(bid.getPrice())
								+ "\n", null);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
    		
        }

        for (Quote ask : event.getSource().getOffers(symbol)) {

    		try {
				textPane.getDocument().insertString(textPane.getDocument().getLength() ,
						"order of size: " + String.valueOf(ask.getSize()) + 
								" moved the ask price to: " + String.valueOf(ask.getPrice())
								+ "\n", null);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
        	
        }

		
	}
}
