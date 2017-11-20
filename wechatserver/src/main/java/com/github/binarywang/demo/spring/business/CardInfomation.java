package com.github.binarywang.demo.spring.business;

public class CardInfomation {
    private String cardId;
    private String cardPassword;
    public CardInfomation(){
        cardId="";
        cardPassword="";
    }
    public CardInfomation(String cardId){
        this.cardId=cardId;
        cardPassword="";
    }
    public CardInfomation(String cardId,String cardPassword){
        this.cardId=cardId;
        this.cardPassword=cardPassword;
    }

    public String getCardPassword() {
        return cardPassword;
    }

    public void setCardPassword(String cardPassword) {
        this.cardPassword = cardPassword;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }
}
