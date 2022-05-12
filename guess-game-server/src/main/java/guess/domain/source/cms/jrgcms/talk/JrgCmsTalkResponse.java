package guess.domain.source.cms.jrgcms.talk;

import java.util.List;

public class JrgCmsTalkResponse {
    private Long total;
    private Long totalElements;
    private List<JrgCmsTalkResponseData> data;

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

    public List<JrgCmsTalkResponseData> getData() {
        return data;
    }

    public void setData(List<JrgCmsTalkResponseData> data) {
        this.data = data;
    }
}
