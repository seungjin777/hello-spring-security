package kr.ac.hansung.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {

    @NotBlank(message = "상품명 입력은 필수입니다.")
    private String name;

    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private int price;

    @Size(min = 5, message = "설명은 5자 이상 입력해야 합니다.")
    private String description;

    @Min(value = 0, message = "재고는 0개 이상이어야 합니다.")
    private int stock;
}
