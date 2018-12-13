package me.exrates.model.dto.mobileApiDto;

/**
 * Created by OLEG on 13.10.2016.
 */
public class MerchantImageShortenedDto {
    private Integer id;
    private String imagePath;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String url) {
        this.imagePath = url;
    }

    @Override
    public String toString() {
        return "MerchantImageShortenedDto{" +
                "id=" + id +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}
