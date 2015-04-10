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
	public void testAddSellToBook() throws StockMarketExpection {
		Market m = new Market("DOW");
		IPO.enterNewStock(m, "SBUX", "Starbucks Inc.", 42.86);
		OrderBook orderbook = new OrderBook(m);

		Trader trader = new Trader("Chris", 30000.0);

		trader.buyFromBank(m, "SBUX", 20);


		SellOrder order = new SellOrder("SBUX", 2, 110.46, trader);

		orderbook.addToOrderBook(order);
		assertTrue(orderbook.sellOrders.containsKey(order.getStockSymbol()));
	}

	@Test
	public void testAddMultipleSellToBook() throws StockMarketExpection{
		Market m = new Market("DOW");
		IPO.enterNewStock(m, "SBUX", "Starbucks Inc.", 92.86);
		OrderBook orderbook = new OrderBook(m);

		Trader trader = new Trader("Chris", 30000.0);
		trader.buyFromBank(m, "SBUX", 20);

		SellOrder order1 = new SellOrder("SBUX", 2, 110.46, trader);
		orderbook.addToOrderBook(order1);

		Trader trader2 = new Trader("Jen", 2000.0);
		trader2.buyFromBank(m, "SBUX", 1);


		SellOrder order2 = new SellOrder("SBUX", 1, 120.33, trader);

		orderbook.addToOrderBook(order2);
		assertTrue(orderbook.sellOrders.containsKey(order1.getStockSymbol()));
	}


	@Test
	public void testTradeZeroMatch() throws StockMarketExpection  {
		Market m = new Market("NASDAQ");
		IPO.enterNewStock(m, "SBUX", "Starbucks Corp.", 92.86);

		// Create 6 traders
		Trader trader1 = new Trader("Chris", 200000.00);
		Trader trader2 = new Trader("Jen", 200000.00);
		Trader trader3 = new Trader("Francis", 200000.00);
		Trader trader4 = new Trader("Thomas", 200000.00);
		Trader trader5 = new Trader("Meg", 200000.00);
		Trader trader6 = new Trader("Kyle", 200000.00);
		Trader trader7 = new Trader("T1", 300000.00); 
		Trader trader8 = new Trader("T2", 300000.00);
		
		trader1.buyFromBank(m, "SBUX", 1600);
		trader2.buyFromBank(m, "SBUX", 600);
		trader3.buyFromBank(m, "SBUX", 600);
		trader7.buyFromBank(m, "SBUX", 1500);
		
		trader1.placeNewOrder(m, "SBUX", 100, 97.0, OrderType.SELL);
		trader2.placeNewOrder(m, "SBUX", 500, 97.5, OrderType.SELL);
		trader3.placeNewOrder(m, "SBUX", 200, 98.0, OrderType.SELL);
		trader7.placeNewMarketOrder(m, "SBUX", 100, 0, OrderType.SELL);

		trader4.placeNewOrder(m, "SBUX", 200, 98.5, OrderType.BUY);
		trader5.placeNewOrder(m, "SBUX", 300, 98.0, OrderType.BUY);
		trader6.placeNewOrder(m, "SBUX", 100, 97.5, OrderType.BUY);
		trader8.placeNewMarketOrder(m, "SBUX", 100, 0, OrderType.BUY);

		m.getOrderBook().trade();
		assertEquals(m.getOrderBook().matchingPrice, 97.5, 0.0);
				
	}
	
	@Test
	public void testTradeZeroMatchMoreEntries() throws StockMarketExpection  {
		Market m = new Market("NASDAQ");
		IPO.enterNewStock(m, "SBUX", "Starbucks Corp.", 92.86);

		// Create 10 traders
		Trader trader1 = new Trader("Chris", 200000.00);
		Trader trader2 = new Trader("Jen", 200000.00);
		Trader trader3 = new Trader("Francis", 200000.00);
		Trader trader4 = new Trader("Thomas", 200000.00);
		Trader trader5 = new Trader("Meg", 200000.00);
		Trader trader6 = new Trader("Kyle", 200000.00);
		Trader trader7 = new Trader("Erica", 200000.00);
		Trader trader8 = new Trader("Lael", 200000.00);
		Trader trader9 = new Trader("T1", 300000.00); 
		Trader trader10 = new Trader("T2", 300000.00);
		
		trader1.buyFromBank(m, "SBUX", 1600);
		trader2.buyFromBank(m, "SBUX", 600);
		trader3.buyFromBank(m, "SBUX", 600);
		trader4.buyFromBank(m, "SBUX", 500);
		trader9.buyFromBank(m, "SBUX", 1500);
		
		trader1.placeNewOrder(m, "SBUX", 100, 97.0,OrderType.SELL);
		trader2.placeNewOrder(m, "SBUX", 500, 97.5, OrderType.SELL);
		trader3.placeNewOrder(m, "SBUX", 200, 98.0, OrderType.SELL);
		trader4.placeNewOrder(m, "SBUX", 100, 98.5, OrderType.SELL);
		trader9.placeNewMarketOrder(m, "SBUX", 100, 0, OrderType.SELL);
		
		trader5.placeNewOrder(m, "SBUX", 200, 98.5, OrderType.BUY);
		trader6.placeNewOrder(m, "SBUX", 300, 98.0, OrderType.BUY);
		trader7.placeNewOrder(m, "SBUX", 100, 97.5, OrderType.BUY);
		trader8.placeNewOrder(m, "SBUX", 100, 97.0, OrderType.BUY);
		trader10.placeNewMarketOrder(m, "SBUX", 100, 0, OrderType.BUY);

		m.getOrderBook().trade();
		assertEquals(m.getOrderBook().matchingPrice, 97.5, 0.0);
				
	}
	
	@Test
	public void testTradeNotZeroMatch() throws StockMarketExpection  {
		Market m = new Market("NASDAQ");
		IPO.enterNewStock(m, "SBUX", "Starbucks Corp.", 92.86);

		// Create 10 traders
		Trader trader1 = new Trader("Chris", 200000.00);
		Trader trader2 = new Trader("Jen", 200000.00);
		Trader trader3 = new Trader("Francis", 200000.00);
		Trader trader4 = new Trader("Thomas", 200000.00);
		Trader trader5 = new Trader("Meg", 200000.00);
		Trader trader6 = new Trader("Kyle", 200000.00);
		Trader trader7 = new Trader("Erica", 200000.00);
		Trader trader8 = new Trader("Lael", 200000.00);
		Trader trader9 = new Trader("T1", 300000.00); 
		Trader trader10 = new Trader("T2", 300000.00);
		
		trader1.buyFromBank(m, "SBUX", 1600);
		trader2.buyFromBank(m, "SBUX", 600);
		trader3.buyFromBank(m, "SBUX", 600);
		trader4.buyFromBank(m, "SBUX", 800);
		trader9.buyFromBank(m, "SBUX", 1700);
		
		trader1.placeNewOrder(m, "SBUX", 700, 98.0,OrderType.SELL);
		trader2.placeNewOrder(m, "SBUX", 300, 98.5, OrderType.SELL);
		trader3.placeNewOrder(m, "SBUX", 500, 99.0, OrderType.SELL);
		trader4.placeNewOrder(m, "SBUX", 700, 99.5, OrderType.SELL);
		trader9.placeNewMarketOrder(m, "SBUX", 1500, 0, OrderType.SELL);
		
		trader5.placeNewOrder(m, "SBUX", 1300, 99.5, OrderType.BUY);
		trader6.placeNewOrder(m, "SBUX", 900, 99.0, OrderType.BUY);
		trader7.placeNewOrder(m, "SBUX", 1000, 98.5, OrderType.BUY);
		trader8.placeNewOrder(m, "SBUX", 900, 98.0, OrderType.BUY);
		trader10.placeNewMarketOrder(m, "SBUX", 100, 0, OrderType.BUY);

		m.getOrderBook().trade();
		assertEquals(m.getOrderBook().matchingPrice, 98.5, 0.0);
				
	}
	
	@Test
	public void testTradeSkewedSellMarket() throws StockMarketExpection  {
		Market m = new Market("NASDAQ");
		IPO.enterNewStock(m, "SBUX", "Starbucks Corp.", 92.86);
		// Create 10 traders
		Trader trader1 = new Trader("Chris", 200000.00);
		Trader trader2 = new Trader("Jen", 200000.00);
		Trader trader3 = new Trader("Francis", 200000.00);
		Trader trader4 = new Trader("Thomas", 200000.00);
		Trader trader5 = new Trader("Meg", 200000.00);
		Trader trader6 = new Trader("Kyle", 200000.00);
		Trader trader7 = new Trader("T1", 300000.00); 
		Trader trader8 = new Trader("T2", 300000.00);
		
		trader1.buyFromBank(m, "SBUX", 1600);
		trader2.buyFromBank(m, "SBUX", 600);
		trader3.buyFromBank(m, "SBUX", 600);
		trader7.buyFromBank(m, "SBUX", 1500);
		
		trader1.placeNewOrder(m, "SBUX", 100, 98.0,OrderType.SELL);
		trader2.placeNewOrder(m, "SBUX", 100, 98.5, OrderType.SELL);
		trader3.placeNewOrder(m, "SBUX", 100, 99.0, OrderType.SELL);
		trader7.placeNewMarketOrder(m, "SBUX", 100, 0, OrderType.SELL);
			
		trader4.placeNewOrder(m, "SBUX", 200, 98.0, OrderType.BUY);
		trader5.placeNewOrder(m, "SBUX", 100, 97.5, OrderType.BUY);
		trader6.placeNewOrder(m, "SBUX", 100, 97.0, OrderType.BUY);
		trader8.placeNewMarketOrder(m, "SBUX", 100, 0, OrderType.BUY);

		m.getOrderBook().trade();
		assertEquals(m.getOrderBook().matchingPrice, 98.0, 0.0);
	}
	
	@Test
	public void testTradeSkewedBuyMarket() throws StockMarketExpection  {
		Market m = new Market("NASDAQ");
		IPO.enterNewStock(m, "SBUX", "Starbucks Corp.", 92.86);

		// Create 10 traders
		Trader trader1 = new Trader("Chris", 200000.00);
		Trader trader2 = new Trader("Jen", 200000.00);
		Trader trader3 = new Trader("Francis", 200000.00);
		Trader trader4 = new Trader("Thomas", 200000.00);
		Trader trader5 = new Trader("Meg", 200000.00);
		Trader trader6 = new Trader("Kyle", 200000.00);
		Trader trader7 = new Trader("T1", 300000.00); 
		Trader trader8 = new Trader("T2", 300000.00);
		
		trader1.buyFromBank(m, "SBUX", 1600);
		trader2.buyFromBank(m, "SBUX", 600);
		trader3.buyFromBank(m, "SBUX", 600);
		trader7.buyFromBank(m, "SBUX", 1500);
		
		trader1.placeNewOrder(m, "SBUX", 100, 97.0,OrderType.SELL);
		trader2.placeNewOrder(m, "SBUX", 100, 97.5, OrderType.SELL);
		trader3.placeNewOrder(m, "SBUX", 100, 98.0, OrderType.SELL);
		trader7.placeNewMarketOrder(m, "SBUX", 100, 0, OrderType.SELL);
		
		trader4.placeNewOrder(m, "SBUX", 200, 99.0, OrderType.BUY);
		trader5.placeNewOrder(m, "SBUX", 100, 98.5, OrderType.BUY);
		trader6.placeNewOrder(m, "SBUX", 100, 98.0, OrderType.BUY);
		trader8.placeNewMarketOrder(m, "SBUX", 100, 0, OrderType.BUY);
		
		m.getOrderBook().trade();
		assertEquals(m.getOrderBook().matchingPrice, 98.5, 0.0);
				
	}
	
	@Test
	public void testTradeNoMatchingEntries() throws StockMarketExpection  {
		Market m = new Market("NASDAQ");
		IPO.enterNewStock(m, "SBUX", "Starbucks Corp.", 92.86);

		// Create 10 traders
		Trader trader1 = new Trader("Chris", 200000.00);
		Trader trader2 = new Trader("Jen", 200000.00);
		Trader trader3 = new Trader("Francis", 200000.00);
		Trader trader4 = new Trader("Thomas", 200000.00);
		Trader trader5 = new Trader("Meg", 200000.00);
		Trader trader6 = new Trader("Kyle", 200000.00);
		Trader trader7 = new Trader("T1", 300000.00); 
		Trader trader8 = new Trader("T2", 300000.00);
		
		trader1.buyFromBank(m, "SBUX", 1600);
		trader2.buyFromBank(m, "SBUX", 600);
		trader3.buyFromBank(m, "SBUX", 600);
		trader7.buyFromBank(m, "SBUX", 1500);
		
		trader1.placeNewOrder(m, "SBUX", 100, 98.0,OrderType.SELL);
		trader2.placeNewOrder(m, "SBUX", 100, 99.0, OrderType.SELL);
		trader3.placeNewOrder(m, "SBUX", 100, 99.5, OrderType.SELL);
		trader7.placeNewMarketOrder(m, "SBUX", 100, 0, OrderType.SELL);
		
		trader4.placeNewOrder(m, "SBUX", 200, 98.5, OrderType.BUY);
		trader5.placeNewOrder(m, "SBUX", 100, 97.5, OrderType.BUY);
		trader6.placeNewOrder(m, "SBUX", 100, 97.0, OrderType.BUY);
		trader8.placeNewMarketOrder(m, "SBUX", 100, 0, OrderType.BUY);
		
		m.getOrderBook().trade();
		assertEquals(m.getOrderBook().matchingPrice, 98.0, 0.0);
				
	}

}