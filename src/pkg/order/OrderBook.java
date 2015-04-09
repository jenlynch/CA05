package pkg.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import pkg.exception.StockMarketExpection;
import pkg.market.Market;
import pkg.market.api.PriceSetter;

public class OrderBook {
	Market m;
	PriceSetter ps;
	HashMap<String, ArrayList<Order>> buyOrders;
	HashMap<String, ArrayList<Order>> sellOrders;

	public OrderBook(Market m) {
		this.m = m;
		buyOrders = new HashMap<String, ArrayList<Order>>();
		sellOrders = new HashMap<String, ArrayList<Order>>();
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

		// 2. Find the matching price
		// 3. Update the stocks price in the market using the PriceSetter.
		// Note that PriceSetter follows the Observer pattern. Use the pattern.
		// 4. Remove the traded orders from the orderbook
		// 5. Delegate to trader that the trade has been made, so that the
		// trader's orders can be placed to his possession (a trader's position
		// is the stocks he owns)
		// (Add other methods as necessary)
		for (String stock : sellOrders.keySet()) {
			if (buyOrders.containsKey(stock)) {
				ArrayList<Order> buying = buyOrders.get(stock);
				ArrayList<Order> selling = sellOrders.get(stock);
				ArrayList<Order> orders;

				TreeMap<Double, ArrayList<Order>> sorted = new TreeMap<Double, ArrayList<Order>>();


				// populate sorted with both buy and sell orders for each price
				for (Order buyOrder : buying) {
					// get arraylist of orders for price
					if (sorted.containsKey(buyOrder.getPrice())) {
						orders = sorted.get(buyOrder.getPrice());
					} else { // if none exists, create a new arraylist
						orders = new ArrayList<Order>();
					}
					orders.add(buyOrder);
					sorted.put(buyOrder.getPrice(), orders);
				}
				for (Order sellOrder : selling) {
					// get arraylist of orders for price
					if (sorted.containsKey(sellOrder.getPrice())) {
						orders = sorted.get(sellOrder.getPrice());
					} else { // if none exists, create a new arraylist
						orders = new ArrayList<Order>();
					}
					orders.add(sellOrder);
					sorted.put(sellOrder.getPrice(), orders);
				}

				// handle and remove market orders
				double marketPrice = m.getStockForSymbol(stock).getPrice();
				ArrayList<Order> marketOrders = sorted.remove(0.0);
				int runningBuyTotal = 0, runningSellTotal = 0;

				for (Order marketOrder : marketOrders) {
					if (marketOrder instanceof BuyOrder) {
						runningBuyTotal += marketOrder.getSize();
					} else {
						runningSellTotal += marketOrder.getSize();
					}
				}

				// construct cumulative least favorably price list
				int numPrices = sorted.size();
				int[] cumulativeBuysPerPrice = new int[numPrices];
				int[] cumulativeSellsPerPrice = new int[numPrices];

				// add up cumulative sell orders at each price
				int i = 0;
				for (double price : sorted.keySet()) {
					ArrayList<Order> ordersAtPrice = sorted.get(price);

					for (Order o : ordersAtPrice) {
						if (o instanceof SellOrder) {
							runningSellTotal += o.getSize();
						}
					}

					cumulativeSellsPerPrice[i] = runningSellTotal;
					i++;
				}

				// add up cumulative sell orders at each price
				int j = numPrices - 1;
				for (double price : sorted.descendingKeySet()) {
					ArrayList<Order> ordersAtPrice = sorted.get(price);

					for (Order o : ordersAtPrice) {
						if (o instanceof BuyOrder) {
							runningBuyTotal += o.getSize();
						}
					}

					cumulativeBuysPerPrice[j] = runningBuyTotal;
					j--;
				}


				// find matching price
				int delta = Integer.MAX_VALUE;
				int k = 0, matchingIndex = -1;
				double matchingPrice = marketPrice;

				while (delta > 0 && k < numPrices) {
					int newDelta = cumulativeBuysPerPrice[k] - cumulativeSellsPerPrice[k];
					if (newDelta < delta) {
						delta = newDelta;
						if (newDelta >= 0) {
							matchingIndex = k;
						}
					}
					k++;
				}
				for (double price : sorted.keySet()) {
					if (matchingIndex == 0) {
						matchingPrice = price;
						break;
					}
					matchingIndex--;
				}

				PriceSetter priceSetter = new PriceSetter();
				priceSetter.registerObserver(m.getMarketHistory());
				m.getMarketHistory().setSubject(priceSetter);
				if (matchingPrice != marketPrice) {
					priceSetter.setNewPrice(m, stock, matchingPrice);
				}

				// remove traded orders from orderbook and delegate to trader
				for (Order marketOrder : marketOrders) { // remove market trades
					if (marketOrder instanceof BuyOrder) {
						buyOrders.get(stock).remove(marketOrder);
					} else {
						sellOrders.get(stock).remove(marketOrder);
					}

					try { // delegate to trader
						marketOrder.getTrader().tradePerformed(marketOrder, matchingPrice);
					} catch (StockMarketExpection e) {
						e.printStackTrace();
					}
				}

				for (double price : sorted.keySet()) { // remove all other executed trades
					ArrayList<Order> ordersAtPrice = sorted.get(price);

					for (Order o : ordersAtPrice) {
						if (o instanceof BuyOrder && price >= matchingPrice) {
							buyOrders.get(stock).remove(o);

							try { // delegate to trader
								o.getTrader().tradePerformed(o, matchingPrice);
							} catch (StockMarketExpection e) {
								e.printStackTrace();
							}
						} else if (o instanceof SellOrder && price <= matchingPrice) {
							sellOrders.get(stock).remove(o);

							try { // delegate to trader
								o.getTrader().tradePerformed(o, matchingPrice);
							} catch (StockMarketExpection e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}
}
			
		
	





