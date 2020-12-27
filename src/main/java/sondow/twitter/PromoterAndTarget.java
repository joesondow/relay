package sondow.twitter;

import java.util.Objects;

public class PromoterAndTarget {

    private final String promoter;
    private final String target;

    public PromoterAndTarget(String promoter, String target) {
        this.promoter = promoter;
        this.target = target;
    }

    public String getPromoter() {
        return promoter;
    }

    public String getTarget() {
        return target;
    }

    @Override public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        PromoterAndTarget that = (PromoterAndTarget) o;
        return Objects.equals(promoter, that.promoter) &&
                Objects.equals(target, that.target);
    }

    @Override public int hashCode() {
        return Objects.hash(promoter, target);
    }

    @Override public String toString() {
        return "PromoterAndTarget{" +
                "promoter='" + promoter + '\'' +
                ", target='" + target + '\'' +
                '}';
    }
}
