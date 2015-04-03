package pkg.order;

import pkg.exception.StockMarketExpection;
import pkg.trader.Trader;

public class BuyOrder extends Order {

	public BuyOrder(String stockSymbol, int size, double price, Trader trader) {
		this.size = size;
		this.stockSymbol = stockSymbol;
		this.price = price;
		this.trader = trader;
		this.orderNumber = getNextOrderNumber();
	}

	public BuyOrder(String stockSymbol, int size, boolean isMarketOrder,
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
	}

	public void printOrder() {
		System.out.println("Stock: " + stockSymbol + " $" + price + " x "
				+ size + " (Buy)");
	}

}
