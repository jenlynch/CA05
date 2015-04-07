package pkg.trader;

import java.util.ArrayList;

import pkg.exception.StockMarketExpection;
import pkg.market.Market;
import pkg.order.BuyOrder;
import pkg.order.Order;
import pkg.order.OrderType;
import pkg.order.SellOrder;
import pkg.util.OrderUtility;

public class Trader {
	// Name of the trader
	String name;
	// Cash left in the trader's hand
	double cashInHand;
	// Stocks owned by the trader
	ArrayList<Order> position;
	// Orders placed by the trader
	ArrayList<Order> ordersPlaced;

	public Trader(String name, double cashInHand) {
		super();
		this.name = name;
		this.cashInHand = cashInHand;
		this.position = new ArrayList<Order>();
		this.ordersPlaced = new ArrayList<Order>();
	}

	public void buyFromBank(Market m, String symbol, int volume)
			throws StockMarketExpection {

		double price = m.getStockForSymbol(symbol).getPrice();
		if (price > this.cashInHand) {
			throw new StockMarketExpection(this.name + " cannot afford to buy this stock");
		}
		Order order = new BuyOrder(symbol, volume, price , this);
		this.position.add(order);
		this.cashInHand -= price * volume;

		// Buy stock straight from the bank
		// Need not place the stock in the order list
		// Add it straight to the user's position
		// If the stock's price is larger than the cash possessed, then an
		// exception is thrown
		// Adjust cash possessed since the trader spent money to purchase a
		// stock.
	}

	public void placeNewOrder(Market m, String symbol, int volume,
			double price, OrderType orderType) throws StockMarketExpection {
		// Place a new order and add to the orderlist
		// Also enter the order into the orderbook of the market.
		// Note that no trade has been made yet. The order is in suspension
		// until a trade is triggered.
		//
		// If the stock's price is larger than the cash possessed, then an
		// exception is thrown
		// A trader cannot place two orders for the same stock, throw an
		// exception if there are multiple orders for the same stock.
		// Also a person cannot place a sell order for a stock that he does not
		// own. Or he cannot sell more stocks than he possesses. Throw an
		// exception in these cases.

		//TODO check to see if trader is trying to sell more stock than he owns
		Order order = null;

		if (orderType == OrderType.BUY) {
			order = new BuyOrder(symbol, volume, price, this);
		}
		else if (orderType == OrderType.SELL) {
			if (OrderUtility.owns(this.position, symbol)) {
				if (OrderUtility.ownedQuantity(this.position, symbol) <= volume) {
					order = new SellOrder(symbol, volume, price, this);
				}
				else {
					throw new StockMarketExpection("Sell order volume is larger than amount of stock owned");
				}
			}
			else {
				throw new StockMarketExpection(this.name + " cannot place a sell order for a stock not owned");
			}
		}

		if (order == null) {
			throw new StockMarketExpection("Not a valid order type");
		}
		else {
			if (OrderUtility.isAlreadyPresent(this.ordersPlaced, order)) {
				throw new StockMarketExpection("There is already an order for this stock");
			}
			else {
				this.ordersPlaced.add(order);
				m.addOrder(order);
			}

		}
	}

	public void placeNewMarketOrder(Market m, String symbol, int volume,
			double price, OrderType orderType) throws StockMarketExpection {
		// Similar to the other method, except the order is a market order
	}

	public void tradePerformed(Order o, double matchPrice)
			throws StockMarketExpection {
		// Notification received that a trade has been made, the parameters are
		// the order corresponding to the trade, and the match price calculated
		// in the order book. Note than an order can sell some of the stocks he
		// bought, etc. Or add more stocks of a kind to his position. Handle
		// these situations.

		// Update the trader's orderPlaced, position, and cashInHand members
		// based on the notification.
		Order order = OrderUtility.findAndExtractOrder(this.position, o.getStockSymbol());

		this.ordersPlaced.remove(o);
		double cashMoved = o.getSize() * matchPrice;
		//trader does not own any of this stock. adds new order to position
		if (order == null && order instanceof BuyOrder) {
			this.position.add(o);
			this.cashInHand -= cashMoved;
		}
		/*trader already owns some stock. adds to the order
			in position correspondng to the stock*/
		else if (order instanceof BuyOrder) {
			order.setSize(order.getSize() + o.getSize());
			this.position.add(order);
			this.cashInHand -= cashMoved;
		}
		if (order == null && order instanceof SellOrder) {
			throw new StockMarketExpection("Trader does not own any of this stock to sell");
		}
		/*trader has stock to sell. updates amount of stock trader owns*/
		else if (order instanceof SellOrder){
			order.setSize(order.getSize() - o.getSize());
			this.position.add(order);
			this.cashInHand += cashMoved;
		}
	}

	public void printTrader() {
		System.out.println("Trader Name: " + name);
		System.out.println("=====================");
		System.out.println("Cash: " + cashInHand);
		System.out.println("Stocks Owned: ");
		for (Order o : position) {
			o.printStockNameInOrder();
		}
		System.out.println("Stocks Desired: ");
		for (Order o : ordersPlaced) {
			o.printOrder();
		}
		System.out.println("+++++++++++++++++++++");
		System.out.println("+++++++++++++++++++++");
	}
}
