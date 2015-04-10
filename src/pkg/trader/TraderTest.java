package pkg.trader;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pkg.exception.StockMarketExpection;
import pkg.market.Market;
import pkg.market.api.IPO;
import pkg.order.BuyOrder;
import pkg.order.Order;
import pkg.order.OrderType;
import pkg.order.SellOrder;

public class TraderTest {
	
	private Market m;
	Trader trader1;
	Trader trader2;
	
	

	@Before
	public void setUp() throws Exception {
		m = new Market("NASDAQ");
		IPO.enterNewStock(m, "SBUX", "Starbucks Corp.", 100.00);
		IPO.enterNewStock(m, "TWTR", "Twitter Inc.", 50);
		trader1 = new Trader("Chris", 3000.00);
		trader2 = new Trader("Jen", 10000.00);
	}

	@After
	public void tearDown() throws Exception {
		m = null;
		trader1 = null;
	}

	/* buyFromBank() tests */
	@Test(expected=StockMarketExpection.class)
	public void testBuyFromBankWhereStockPriceLargerThanCashPossessed() throws StockMarketExpection {
		trader1.buyFromBank(m, "SBUX", 40);
		fail("Did not throw exception");
	}
	
	@Test
	public void testBuyFromBankStockAddedToPositionAndCashInHandUpdated() throws StockMarketExpection {
		trader1.buyFromBank(m, "SBUX", 10);
		Order o1 = trader1.position.get(0);
		assertTrue(o1.getStockSymbol() == "SBUX" && o1.getSize() == 10 && o1.getPrice() == m.getStockForSymbol("SBUX").getPrice());
		assertEquals(trader1.cashInHand, 2000.00, 0);
	}
	
	
	/* placeNewOrder() tests */
	@Test(expected=StockMarketExpection.class)
	public void testPlaceNewOrderStockPriceLargerThanCashPossessed() throws StockMarketExpection {
		trader1.placeNewOrder(m, "SBUX", 40, 100.00, OrderType.BUY);
		fail("Did not throw exception");
	}
	
	@Test(expected=StockMarketExpection.class)
	public void testPlaceNewOrderMultipleOrdersForSameStock() throws StockMarketExpection {
		trader1.placeNewOrder(m, "SBUX", 5, 100.00, OrderType.BUY);
		trader1.placeNewOrder(m, "SBUX", 6, 100.00, OrderType.BUY);
		fail("Did not throw exception");
	}
	
	@Test(expected=StockMarketExpection.class)
	public void testPlaceNewSellOrderForStockTraderDoesNotOwn() throws StockMarketExpection {
		trader1.placeNewOrder(m, "SBUX", 5, 100.00, OrderType.SELL);
		fail("Did not throw exception");
	}
	
	@Test(expected=StockMarketExpection.class)
	public void testPlaceNewSellOrderForMoreStockThanPossessed() throws StockMarketExpection {
		Order o = new BuyOrder("SBUX", 10, 100.00, trader1);
		trader1.position.add(o);
		trader1.placeNewOrder(m, "SBUX", 100, 100.00, OrderType.SELL);
		fail("Did not throw exception");
	}
	
	@Test
	public void testPlaceNewOrderAddedToOrderList() throws StockMarketExpection {
		trader1.placeNewOrder(m, "SBUX", 20, 100.00, OrderType.BUY);
		trader1.buyFromBank(m, "TWTR", 5);
		trader1.placeNewOrder(m, "TWTR", 5, 50.00, OrderType.SELL);
		Order o = trader1.ordersPlaced.get(0);
		assertTrue(o.getStockSymbol() == "SBUX" && o.getSize() == 20 && o.getPrice() == 100.00);
		Order o1 = trader1.ordersPlaced.get(1);
		assertTrue(o1.getStockSymbol() == "TWTR" && o1.getSize() == 5 && o1.getPrice() == 50.00);
	}
	
	/* placeNewMarketOrder() tests*/
	@Test(expected=StockMarketExpection.class)
	public void testPlaceNewMarketOrderStockPriceLargerThanCashPossessed() throws StockMarketExpection {
		trader1.placeNewMarketOrder(m, "SBUX", 40, 0.0, OrderType.BUY);
		fail("Did not throw exception");
	}
	
	@Test(expected=StockMarketExpection.class)
	public void testPlaceNewMarketOrderMultipleOrdersForSameStock() throws StockMarketExpection {
		trader1.placeNewMarketOrder(m, "SBUX", 5, 0.0, OrderType.BUY);
		trader1.placeNewMarketOrder(m, "SBUX", 6, 0.0, OrderType.BUY);
		fail("Did not throw exception");
	}
	
	@Test(expected=StockMarketExpection.class)
	public void testPlaceNewMarketSellOrderForStockTraderDoesNotOwn() throws StockMarketExpection {
		trader1.placeNewMarketOrder(m, "SBUX", 5, 0.0, OrderType.SELL);
		fail("Did not throw exception");
	}
	
	@Test(expected=StockMarketExpection.class)
	public void testPlaceNewMarketSellOrderForMoreStockThanPossessed() throws StockMarketExpection {
		Order o = new BuyOrder("SBUX", 10, 0.0, trader1);
		trader1.position.add(o);
		trader1.placeNewMarketOrder(m, "SBUX", 100, 0.0, OrderType.SELL);
		fail("Did not throw exception");
	}
	@Test
	public void testPlaceNewMarketOrderAddedToOrderList() throws StockMarketExpection {
		trader1.placeNewMarketOrder(m, "SBUX", 20, 0, OrderType.BUY);
		trader1.buyFromBank(m, "TWTR", 5);
		trader1.placeNewMarketOrder(m, "TWTR", 5, 0, OrderType.SELL);
		Order o = trader1.ordersPlaced.get(0);
		assertTrue(o.getStockSymbol() == "SBUX" && o.getSize() == 20 && o.getPrice() == 0.0);
		Order o1 = trader1.ordersPlaced.get(1);
		assertTrue(o1.getStockSymbol() == "TWTR" && o1.getSize() == 5 && o1.getPrice() == 0.0);
	}
	
	/* tradePerformed() tests */
	@Test(expected=StockMarketExpection.class)
	public void testTradePerformedOrderDoesNotExistInOrdersPlaced() throws StockMarketExpection {
		Order o = new BuyOrder("SBUX", 10, 100.0, trader1);
		trader1.tradePerformed(o, 100.0);
		fail("Did not throw exception");
	}
	
	@Test
	public void testTradePerformedSellOrderUpdateCashAndCheckRemovedFromPosition() throws StockMarketExpection {
		trader1.buyFromBank(m, "SBUX", 20);
		Order o = new SellOrder("SBUX", 10, 100.00, trader1);
		trader1.ordersPlaced.add(o);
		trader1.tradePerformed(o, 100.0);
		assertEquals(trader1.cashInHand, 2000.00, 0);
		assertFalse(trader1.position.contains(o));
		assertFalse(trader1.ordersPlaced.contains(o));
	}
	
	@Test
	public void testTradePerformedBuyOrderUpdateCashAndCheckAddedToPosition() throws StockMarketExpection {
		Order o = new BuyOrder("SBUX", 10, 100.00, trader1);
		trader1.ordersPlaced.add(o);
		trader1.tradePerformed(o, 100.0);
		assertEquals(trader1.cashInHand, 2000.00, 0);
		assertTrue(trader1.position.contains(o));
		assertFalse(trader1.ordersPlaced.contains(o));
	}
	
	
	
	

	

	
	
	

}
