package ys.cloud.sbot.exchange;

import java.time.Duration;

public enum Interval {
	
	 _1m ("1m" , Duration.ofMinutes(1)),
	 _3m ("3m", Duration.ofMinutes(3)),
	 _5m ("5m", Duration.ofMinutes(5)),
	 _15m ("15m", Duration.ofMinutes(15)),
	 _30m ("30m", Duration.ofMinutes(30)),
	 _1h ("1h", Duration.ofHours(1)),
	 _2h ("2h", Duration.ofHours(2)),
	 _4h ("4h", Duration.ofHours(4)),
	 _6h ("6h", Duration.ofHours(6)),
	 _8h ("8h", Duration.ofHours(8)),
	 _12h ("12h", Duration.ofHours(12)),
	 _1d ("1d", Duration.ofDays(1)),
	 _3d ("3d", Duration.ofDays(3));
//	 _1w ("1w", Duration.ofWeeks(1)),
//	 _1M ("1M", Duration.ofDays(3)),
	 
	final private String text ;
	final private Duration duration;
	
	Interval(String text, Duration duration) {
		this.text = text;
		this.duration = duration;
	}

	public String getText() {
		return text;
	}

	public Duration getDuration() {
		return duration;
	}
	
}
