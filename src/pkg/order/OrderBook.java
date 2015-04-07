package pkg.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import pkg.market.Market;
import pkg.market.api.PriceSetter;

public class OrderBook {
	Market m;
	PriceSetter ps;
	HashMap<String, ArrayList<Order>> buyOrders;
	HashMap<String, ArrayList<Order>> sellOrders;
	HashMap<Double, Integer> cumulativeBuyOrders; //price, total order volume for price
	HashMap<Double, Integer> cumulativeSellOrders; //price, total order volume for price

	int[] cumulativeBuyPerPrice;
	int[] cumulativeSellPerPrice;

	public OrderBook(Market m) {
		this.m = m;
		buyOrders = new HashMap<String, ArrayList<Order>>();
		sellOrders = new HashMap<String, ArrayList<Order>>();
		cumulativeBuyOrders = new HashMap<Double, Integer>();
		cumulativeSellOrders = new HashMap<Double, Integer>();
	}

	public void addToOrderBook(Order order) {
		ArrayList<Order> orderList;
		if (order instanceof BuyOrder) {
			orderList = buyOrders.get(order.getStockSymbol());
			if (orderList == null) {
				orderList = new ArrayList<Order>();
				orderList.add(order);
				buyOrders.put(order.getStockSymbol(), orderList);
			}
			else {
				orderList.add(order);
				buyOrders.put(order.getStockSymbol(), orderList);
			}
		}
		if (order instanceof SellOrder) {
			orderList = sellOrders.get(order.getStockSymbol());
			if (orderList == null) {
				orderList = new ArrayList<Order>();
				orderList.add(order);
				sellOrders.put(order.getStockSymbol(), orderList);
			}
			else {
				orderList.add(order);
				sellOrders.put(order.getStockSymbol(), orderList);
			}
		}
		// Populate the buyOrders and sellOrders data structures, whichever
		// appropriate
	}

	public void trade() {
		// Complete the trading.
		// 1. Follow and create the orderbook data representation (see spec)
		Iterator<Map.Entry<String, ArrayList<Order>>> it = sellOrders.entrySet().iterator();
		double matchPrice = 0.0;
		while (it.hasNext()) {
			Map.Entry<String, ArrayList<Order>> pair = (Map.Entry<String, ArrayList<Order>>)it.next();
			for (Order o : pair.getValue()) {
				updateOrderBook(o);
			}
			matchPrice = findMatchPrice(matchPrice);
			ps.setNewPrice(this.m, pair.getKey(), matchPrice);
			//TODO observer pattern?
			ps.notifyObserver();
			//TODO steps 4 & 5
			it.remove(); // avoids a ConcurrentModificationException
		}
		// 2. Find the matching price
		// 3. Update the stocks price in the market using the PriceSetter.
		// Note that PriceSetter follows the Observer pattern. Use the pattern.
		// 4. Remove the traded orders from the orderbook
		// 5. Delegate to trader that the trade has been made, so that the
		// trader's orders can be placed to his possession (a trader's position
		// is the stocks he owns)
		// (Add other methods as necessary)
	}

	private double findMatchPrice(double matchPrice) {
		int tempMaxTrades = 0;
		for (double price : cumulativeSellOrders.keySet()) {
			int numTrades = Math.abs(cumulativeSellOrders.get(price) - cumulativeBuyOrders.get(price));
			if (numTrades > tempMaxTrades) {
				tempMaxTrades = numTrades;
				matchPrice = price;
			}
		}
		return matchPrice;
	}

	private void updateOrderBook(Order o) {
		if (o instanceof BuyOrder) {
			if (cumulativeBuyOrders.containsKey(o.price)) {
				int volume = cumulativeBuyOrders.get(o.price);
				volume += o.size;
				cumulativeBuyOrders.put(o.price, volume);
			}
			else {
				cumulativeBuyOrders.put(o.price, o.size);
			}
		}
		if (o instanceof SellOrder) {
			if (cumulativeSellOrders.containsKey(o.price)) {
				int volume = cumulativeSellOrders.get(o.price);
				volume += o.size;
				cumulativeSellOrders.put(o.price, volume);
			}
			else {
				cumulativeSellOrders.put(o.price, o.size);
			}
		}
	}

}
