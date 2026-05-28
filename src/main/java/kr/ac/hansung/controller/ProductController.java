package kr.ac.hansung.controller;

import jakarta.validation.Valid;
import kr.ac.hansung.dto.ProductDto;
import kr.ac.hansung.entity.Product;
import kr.ac.hansung.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public String list(
            @RequestParam(required = false) String keyword, //필수는 아니다
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        // 원래는 Pageable 타입으로 받는게 일반적(PageRequest는 Pageable 구현체라 상관없긴 함)
        // 페이징 규칙 정의
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id"));

        // 빈 문자열("")을 null로 정규화
        String normalizedKeyword = (keyword != null && !keyword.isBlank()) ? keyword : null;

        Page<Product> productPage; // 서비스계층에서 받아올 페이지객체
        if(normalizedKeyword != null) {
            // 검색어가 있을 경우 -> 키워드에 해당하는 목록
            productPage = productService.searchProducts(pageRequest, normalizedKeyword);
        } else {
            // 검색어가 없을 경우 -> 전체 목록
            productPage = productService.getProducts(pageRequest);
        }

        //타임리프 html로 전달할 데이터
        model.addAttribute("productPage", productPage);
        model.addAttribute("keyword", normalizedKeyword);
        return "products/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        return "products/detail";
    }

    @GetMapping("/{id}/edit") // 수정하기 위해 수정하려는 제품의 정보를 받아와야함
    public String editProductForm(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        ProductDto dto = new ProductDto();
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setDescription(product.getDescription());
        model.addAttribute("productDto", dto);
        model.addAttribute("productId", id);
        return "products/edit";
    }

    @PostMapping("/{id}/edit") // 수정하려는 정보를 전송
    public String editProduct(@PathVariable Long id,
                              @Valid @ModelAttribute ProductDto productDto,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("productId", id);
            return "products/edit";
        }
        productService.updateProduct(id, productDto);
        ra.addFlashAttribute("successMessage", "상품이 수정되었습니다.");
        return "redirect:/products";
    }



    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("product", new ProductDto());
        return "products/add";
    }

    @PostMapping
    public String save(@ModelAttribute ProductDto dto) {
        productService.save(dto);
        return "redirect:/products";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        productService.deleteById(id);
        return "redirect:/products";
    }
}
