package yi.master.statement.vo;

/**
 * 测试统计
 * @author xuwangcheng
 * @version 2017.11.22, 1.0.0.0
 *
 */
public class StatisticalQuantity {
	
	private String itemName;
	private Integer totalCount;
	private Integer todayCount;
	private Integer yesterdayCount;
	private Integer thisWeekCount;
	private Integer thisMonthCount;
	
	public StatisticalQuantity() {
		super();
		
	}
	
	public StatisticalQuantity(String itemNane) {
		super();
		this.itemName = itemNane;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public Integer getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}
	public Integer getTodayCount() {
		return todayCount;
	}
	public void setTodayCount(Integer todayCount) {
		this.todayCount = todayCount;
	}
	public Integer getYesterdayCount() {
		return yesterdayCount;
	}
	public void setYesterdayCount(Integer yesterdayCount) {
		this.yesterdayCount = yesterdayCount;
	}
	public Integer getThisWeekCount() {
		return thisWeekCount;
	}
	public void setThisWeekCount(Integer thisWeekCount) {
		this.thisWeekCount = thisWeekCount;
	}
	public Integer getThisMonthCount() {
		return thisMonthCount;
	}
	public void setThisMonthCount(Integer thisMonthCount) {
		this.thisMonthCount = thisMonthCount;
	}
	
	
	
}
