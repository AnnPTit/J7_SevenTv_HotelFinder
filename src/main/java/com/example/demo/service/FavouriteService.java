package com.example.demo.service;

public interface FavouriteService {
    boolean isLove(String idCustom, String idRoom);
    boolean setLove(String idCustom, String idRoom);
}
