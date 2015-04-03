package pkg.order;

import pkg.exception.StockMarketExpection;
import pkg.trader.Trader;

public class SellOrder extends Order {
	public SellOrder(String stockSymbol, int size, double price, Trader trader) {
		this.size = size;
		this.stockSymbol = stockSymbol;
		this.price = price;
		this.trader = trader;
		this.orderNumber = getNextOrderNumber();
	}

	public SellOrder(String stockSymbol, int size, boolean isMarketOrder,
			Trader trader) throws StockMarketExpection {
		if(isMarketOrder == false) {
			throw new StockMarketExpection("This order has been placed without a valid price");
		}
		else {
			this.isMarketOrder = true;
		}
		this.size = size;
		this.stockSymbol = stockSymbol;
		this.trader = trader;
		this.orderNumber = getNextOrderNumber();
		this.price = 0.0;
		// TODO:
		/* CAN CLEAN UP LATER*/
		// Create a new sell order which is a market order
		// Set the price to 0.0, Set isMarketOrder attribute to true
		//
		// If this is called with isMarketOrder == false, throw an exception
		// that an order has been placed without a valid price.
	}

	public void printOrder() {
		System.out.println("Stock: " + stockSymbol + " $" + price + " x "
				+ size + " (Sell)");
	}
}
