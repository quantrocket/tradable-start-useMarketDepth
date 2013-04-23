package com.tradable.exampleApps.useMarketDepth;


import java.awt.Color;
import java.math.BigDecimal;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.tradable.api.component.WorkspaceModule;
import com.tradable.api.component.WorkspaceModuleProperties;
import com.tradable.api.component.state.PersistedStateHolder;
import com.tradable.api.services.marketdata.MarketDepthEvent;
import com.tradable.api.services.marketdata.MarketDepthListener;
import com.tradable.api.services.marketdata.MarketDepthService;
import com.tradable.api.services.marketdata.MarketDepthSubscription;
import com.tradable.api.services.marketdata.Quote;
import javax.swing.JTextPane;
import javax.swing.JScrollBar;
import javax.swing.text.BadLocationException;

import java.awt.ScrollPane;
import javax.swing.JScrollPane;

public class UseMarketDepthModule extends JPanel implements WorkspaceModule, MarketDepthListener{
	

	//change this number to something more sensible
	private static final long serialVersionUID = 1L; 
	private MarketDepthService marketDepthService;
	private MarketDepthSubscription marketDepthSubscription;

	private JLabel lblBidVWAP;
	private JLabel lblBidVWAPQuant;
	private JLabel lblAskVWAP;
	private JLabel lblAskVWAPQuant;
	
	////////See marketDepthUpdated(..) method to see what those are for//////
	private int volumeToTrade = 25000000; //volumeToTrade
	
	private int bidTradeQuantity = 0;
	double bidVWP = 0.0; //non averaged yet
	private BigDecimal bidVWAP;
	
	private int askTradeQuantity = 0;
	double askVWP = 0.0;
	private BigDecimal askVWAP;
	/////////////////////////////////////////////////////////////////////////
	
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
		
		
		volumeToTrade = 19000000; //chosen arbitrarily
		bidTradeQuantity = 0;
		bidVWP = 0.0; 
		askTradeQuantity = 0;
		askVWP = 0.0;

		
		
        String symbol = event.getSymbol();
        
        //we calculate the VWAP for the bid price using the standard
        //VWAP formula. The final value is stored in a BigDecimal object,
        //We note that theoretically, the bid VWAP will always be <= to the 
        //smaller volume bid (as seen in the Order Entry App for instance)
        //the converse is true for the ask VWAP.
		for (Quote bid : event.getSource().getBids(symbol)){
			
			if(bid.getSize() > volumeToTrade){
				bidTradeQuantity += volumeToTrade;
				bidVWP += volumeToTrade*bid.getPrice();
				volumeToTrade = 0;
			}
			else{
				bidTradeQuantity += bid.getSize();
				bidVWP += bid.getPrice()*bid.getSize();
				volumeToTrade -= bid.getSize();
			}
			
			if(volumeToTrade == 0)
				break;
		
		}
         
		//Please note, MarketDepth data will be provided
		//for the chosen symbol for a maximum of 18M.
		//This maximum is almost always provided although
		//There might be some exceptions.
		//For other symbols, the provided liquidity might be different.
		//e.g. XAUUSD will only provide up to 1k.
		bidVWAP = new BigDecimal(bidVWP/bidTradeQuantity);
		bidVWAP = bidVWAP.setScale(5, BigDecimal.ROUND_HALF_UP);
		lblBidVWAP.setText("Bid VWAP: " + bidVWAP.toPlainString());
		lblBidVWAPQuant.setText("found bid liquidity for " + String.valueOf(bidTradeQuantity)
				+ " of " + String.valueOf(volumeToTrade + bidTradeQuantity));
		
		
		volumeToTrade += bidTradeQuantity; //volumeToTrade
	
        
		for (Quote ask : event.getSource().getOffers(symbol)){
			
			if(ask.getSize() > volumeToTrade){
				askTradeQuantity += volumeToTrade;
				askVWP += volumeToTrade*ask.getPrice();
				volumeToTrade = 0;
			}
			else{
				askTradeQuantity += ask.getSize();
				askVWP += ask.getPrice()*ask.getSize();
				volumeToTrade -= ask.getSize();
			}
			
			if(volumeToTrade == 0)
				break;

		}
        
		askVWAP = new BigDecimal(askVWP /= askTradeQuantity);
		askVWAP = askVWAP.setScale(5, BigDecimal.ROUND_HALF_UP);
		lblAskVWAP.setText("Ask VWAP: " + askVWAP.toPlainString());
		lblAskVWAPQuant.setText("found ask liquidity for " + String.valueOf(askTradeQuantity)
				+ " of " + String.valueOf(volumeToTrade + askTradeQuantity));

		
	}
}
