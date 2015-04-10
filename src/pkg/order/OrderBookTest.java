package pkg.order;

import static org.junit.Assert.*;

import org.junit.Test;

import pkg.exception.StockMarketExpection;
import pkg.market.Market;
import pkg.market.api.IPO;
import pkg.trader.Trader;

public class OrderBookTest {

	@Test
	public void testAddBuyToBook() {
		Trader trader = new Trader("Chris", 300.0);
		BuyOrder order = new BuyOrder("SBUX", 2, 92.86, trader);
		
		Market m = new Market("DOW");
		OrderBook orderbook = new OrderBook(m);
		
		orderbook.addToOrderBook(order);
		assertTrue(orderbook.buyOrders.containsKey(order.getStockSymbol()));
	}
	
	@Test
	public void testAddMultipleBuyToBook() {
		Trader trader = new Trader("Jen", 400.0);
		BuyOrder order1 = new BuyOrder("GILD", 3, 93.33, trader);
		
		Market m = new Market("DOW");
		OrderBook orderbook = new OrderBook(m);
		
		orderbook.addToOrderBook(order1);
		
		Trader trader2 = new Trader("Chris", 200.0);
		BuyOrder order2 = new BuyOrder("TWTR", 2, 93.33, trader2);
		
		orderbook.addToOrderBook(order2);
		assertTrue(orderbook.buyOrders.containsKey(order1.getStockSymbol()));
	}
	
	@Test
	public void testAddSellToBook() {
		Market m = new Market("DOW");
		IPO.enterNewStock(m, "SBUX", "Starbucks Inc.", 42.86);
		OrderBook orderbook = new OrderBook(m);
		
		Trader trader = new Trader("Chris", 30000.0);
		try 
		{
			trader.buyFromBank(m, "SBUX", 20);
		} 
		catch (StockMarketExpection e) {
			e.printStackTrace();
		}
		
		SellOrder order = new SellOrder("SBUX", 2, 110.46, trader);
		
		orderbook.addToOrderBook(order);
		assertTrue(orderbook.sellOrders.containsKey(order.getStockSymbol()));
	}
	
	@Test
	public void testAddMultipleSellToBook() {
		Market m = new Market("DOW");
		IPO.enterNewStock(m, "SBUX", "Starbucks Inc.", 92.86);
		OrderBook orderbook = new OrderBook(m);
		
		Trader trader = new Trader("Chris", 30000.0);
		try {
			trader.buyFromBank(m, "SBUX", 20);
		} 
		catch (StockMarketExpection e) {
			e.printStackTrace();
		}
		
		SellOrder order1 = new SellOrder("SBUX", 2, 110.46, trader);
		orderbook.addToOrderBook(order1);
		
		Trader trader2 = new Trader("Jen", 2000.0);
		try {
			trader2.buyFromBank(m, "SBUX", 1);
		} 
		catch (StockMarketExpection e) {
			e.printStackTrace();
		}
		
		SellOrder order2 = new SellOrder("SBUX", 1, 120.33, trader);
		
		orderbook.addToOrderBook(order2);
		assertTrue(orderbook.sellOrders.containsKey(order1.getStockSymbol()));
	}

}