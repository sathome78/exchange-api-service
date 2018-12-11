package me.exrates.model.dto.filterData;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static me.exrates.model.dto.filterData.FilterDataItem.IN_FORMAT;
import static me.exrates.model.dto.filterData.FilterDataItem.LIKE_FORMAT_MIDDLE;

/**
 * Created by Maks on 26.12.2017.
 */
@Getter
@Setter
@NoArgsConstructor
public class RefillAddressFilterData extends TableFilterData {

    private String address;
    private String email;
    private List<Integer> currencyIds;

    @Override
    public void initFilterItems() {
        FilterDataItem[] items = new FilterDataItem[] {
                new FilterDataItem("address", "RRA.address LIKE", address, LIKE_FORMAT_MIDDLE),
                new FilterDataItem("currency_ids", "RRA.currency_id IN", currencyIds, IN_FORMAT),
                new FilterDataItem("email", "USER.email LIKE", email, LIKE_FORMAT_MIDDLE)
        };
        populateFilterItemsNonEmpty(items);
    }

}
