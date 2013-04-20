package com.tradable.exampleApps.useMarketDepth;


import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

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

	private JLabel lblBidVWAP;
	private JLabel lblBidVWAPQuant;
	private JLabel lblAskVWAP;
	private JLabel lblAskVWAPQuant;
	
	public UseMarketDepthModule(MarketDepthService marketDepthService) {
		setLayout(null);
		setSize(400, 400);
		setModuleTitle("Use MarketDepth");
		putClientProperty(WorkspaceModuleProperties.COMPONENT_RESIZE_ENABLED, false);
		
		//creates an empty Jlabel that will contain the accountID upon 
		//catching an event.
		
		lblBidVWAP = new JLabel("BW");
		lblBidVWAP.setHorizontalAlignment(SwingConstants.LEADING);
		lblBidVWAP.setForeground(Color.BLACK);
		lblBidVWAP.setBounds(64, 28, 141, 31);
		add(lblBidVWAP);
		
		
		
		this.marketDepthService = marketDepthService;	
		this.marketDepthSubscription = this.marketDepthService.createMarketDepthSubscription();
		this.marketDepthSubscription.addSymbol("EURUSD");
		this.marketDepthSubscription.setListener(this);
		
		lblBidVWAPQuant = new JLabel("BidQuant");
		lblBidVWAPQuant.setHorizontalAlignment(SwingConstants.LEADING);
		lblBidVWAPQuant.setBounds(64, 80, 319, 31);
		add(lblBidVWAPQuant);
		
		lblAskVWAP = new JLabel("AskVWAP");
		lblAskVWAP.setBounds(64, 201, 141, 31);
		add(lblAskVWAP);
		
		lblAskVWAPQuant = new JLabel("AskQuant");
		lblAskVWAPQuant.setBounds(64, 259, 319, 31);
		add(lblAskVWAPQuant);
			
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
		
		
		int vtt = 25000000; //volumeToTrade
		int bidTradeQuantity = 0;
		double bidVWP = 0; //non averaged yet
		double bidVWAP = 0;
	
        String symbol = event.getSymbol();
        
		for (Quote bid : event.getSource().getBids(symbol)){
			
			if(bid.getPrice() > vtt){
				bidTradeQuantity += vtt;
				bidVWP += vtt*bid.getPrice();
				vtt = 0;
			}
			else{
				bidTradeQuantity += bid.getSize();
				bidVWP += bid.getPrice()*bid.getSize();
				vtt -= bid.getSize();
			}
			
			if(vtt == 0)
				break;
		}
        
		bidVWAP = bidVWP/bidTradeQuantity;
		lblBidVWAP.setText("Bid VWAP: " + String.valueOf(bidVWAP));
		lblBidVWAPQuant.setText("found bid liquidity for " + String.valueOf(bidTradeQuantity)
				+ " of " + String.valueOf(vtt + bidTradeQuantity));
		
		
		vtt += bidTradeQuantity; //volumeToTrade
		int askTradeQuantity = 0;
		double askVWP = 0; //non averaged yet
		double askWAP = 0;
	
        
		for (Quote ask : event.getSource().getOffers(symbol)){
			
			if(ask.getPrice() > vtt){
				askTradeQuantity += vtt;
				askVWP += vtt*ask.getPrice();
				vtt = 0;
			}
			else{
				askTradeQuantity += ask.getSize();
				askVWP += ask.getPrice()*ask.getSize();
				vtt -= ask.getSize();
			}
			
			if(vtt == 0)
				break;
		}
        
		askWAP = askVWP/askTradeQuantity;
		lblAskVWAP.setText("Ask VWAP: " + String.valueOf(askWAP));
		lblAskVWAPQuant.setText("found ask liquidity for " + String.valueOf(askTradeQuantity)
				+ " of " + String.valueOf(vtt + askTradeQuantity));

		
	}
}
