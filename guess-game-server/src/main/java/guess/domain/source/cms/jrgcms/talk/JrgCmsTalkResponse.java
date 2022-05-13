package guess.domain.source.cms.jrgcms.talk;

import java.util.List;

public class JrgCmsTalkResponse {
    private Long total;
    private Long totalElements;
    private List<JrgCmsActivity> data;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    public List<JrgCmsActivity> getData() {
        return data;
    }

    public void setData(List<JrgCmsActivity> data) {
        this.data = data;
    }
}
