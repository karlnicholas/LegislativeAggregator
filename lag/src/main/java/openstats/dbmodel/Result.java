package openstats.dbmodel;

import java.math.BigDecimal;

import javax.persistence.*;

@Embeddable
public class Result {
	@Basic
	private String value;
	@Basic
	private String error;
	public Result() {}
	public Result(BigDecimal value, BigDecimal error) {
		this.value = value.toString();
		this.error = error.toString();
	}
	// deep clone
	public Result(Result result) {
		value = result.getValue().toString();
		error = result.getError().toString();
	}

	public Result add(Result result) {
		BigDecimal tVal = new BigDecimal(value);
		value = tVal.add(result.getValue()).toString();

		BigDecimal tErr = new BigDecimal(error);
		error = tErr.add(result.getError()).toString();

		return this;
	}
	public BigDecimal getValue() {
		return new BigDecimal(value);
	}
	public void setValue(BigDecimal value) {
		this.value = value.toString(); 
	}
	public BigDecimal getError() {
		return new BigDecimal(error);
	}
	public void setError(BigDecimal error) {
		this.error = error.toString(); 
	}
}
