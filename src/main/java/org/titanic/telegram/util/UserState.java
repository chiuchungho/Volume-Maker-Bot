package org.titanic.telegram.util;

import org.titanic.telegram.dto.UserData;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Hanno Skowronek, Chung Ho Chiu
 */
public class UserState {
//    public static final int INIT_STATE = 0;
    public static final int INPUT_SYMBOL_STATE = 1;
    public static final int INPUT_VOLUME_STATE = 2;
    public static final int INPUT_DURATION_HOUR_MIN_STATE = 3;
    public static final int INPUT_DURATION_HOUR_MAX_STATE = 4;
    public static final int INPUT_PRICE_MIN_STATE = 5;
    public static final int INPUT_PRICE_MAX_STATE = 6;
    public static final int INPUT_NUMBER_OF_TRADES_MIN_STATE = 7;
    public static final int INPUT_NUMBER_OF_TRADES_MAX_STATE = 8;
    public static final int INPUT_CONFIRM_CREATE_STATE = 9;

    public static final int INPUT_CONFIRM_STOP_ALL_STATE = 100;
    public static final int INPUT_STOP_BY_ID_STATE = 101;
    public static final int INPUT_CONFIRM_STOP_BY_ID_STATE = 102;

    public static ConcurrentHashMap<String, UserData> map = new ConcurrentHashMap<>();

    public static int getUserState(String username){
        if(map.get(username) == null){
            return 0;
        }else{
            return UserState.map.get(username).getState();
        }
    }

    public static void setUserState(String username, int state){
        if(map.get(username) == null){
            UserData userData = new UserData();
            userData.setState(state);
            map.put(username, userData);
        }else{
            UserData userData = map.get(username);
            userData.setState(state);
            map.put(username, userData);
        }
    }

    public static void setSymbol(String username, String symbol){
        if(map.get(username) != null){
            UserData userData = map.get(username);
            userData.setSymbol(symbol);
            map.put(username, userData);
        }
    }

    public static String getSymbol(String username){
        if(map.get(username) != null){
            UserData userData = map.get(username);
            return userData.getSymbol();
        }
        return null;
    }

    public static void setDurationHourMin(String username, int durationHourMin){
        if(map.get(username) != null){
            UserData userData = map.get(username);
            userData.setDurationHourMin(durationHourMin);
            map.put(username, userData);
        }
    }

    public static int getDurationHourMin(String username){
        if(map.get(username) != null){
            UserData userData = map.get(username);
            return userData.getDurationHourMin();
        }
        return 0;
    }

    public static void setDurationHourMax(String username, int durationHourMax){
        if(map.get(username) != null){
            UserData userData = map.get(username);
            userData.setDurationHourMax(durationHourMax);
            map.put(username, userData);
        }
    }

    public static int getDurationHourMax(String username){
        if(map.get(username) != null){
            UserData userData = map.get(username);
            return userData.getDurationHourMax();
        }
        return 0;
    }

    public static void setPriceMin(String username, double price){
        if(map.get(username) != null){
            UserData userData = map.get(username);
            userData.setPriceMin(price);
            map.put(username, userData);
        }
    }

    public static double getPriceMin(String username){
        if(map.get(username) != null){
            UserData userData = map.get(username);
            return userData.getPriceMin();
        }
        return 0;
    }

    public static void setPriceMax(String username, double price){
        if(map.get(username) != null){
            UserData userData = map.get(username);
            userData.setPriceMax(price);
            map.put(username, userData);
        }
    }

    public static double getPriceMax(String username){
        if(map.get(username) != null){
            UserData userData = map.get(username);
            return userData.getPriceMax();
        }
        return 0;
    }

    public static void setVolume(String username, double volume){
        if(map.get(username) != null){
            UserData userData = map.get(username);
            userData.setVolume(volume);
            map.put(username, userData);
        }
    }

    public static double getVolume(String username){
        if(map.get(username) != null){
            UserData userData = map.get(username);
            return userData.getVolume();
        }
        return 0;
    }

    public static void setNumberOfTradesMin(String username, int numberOfTradesMin){
        if(map.get(username) != null){
            UserData userData = map.get(username);
            userData.setNumberOfTradesMin(numberOfTradesMin);
            map.put(username, userData);
        }
    }

    public static int getNumberOfTradesMin(String username){
        if(map.get(username) != null){
            UserData userData = map.get(username);
            return userData.getNumberOfTradesMin();
        }
        return 0;
    }

    public static void setNumberOfTradesMax(String username, int numberOfTradesMax){
        if(map.get(username) != null){
            UserData userData = map.get(username);
            userData.setNumberOfTradesMax(numberOfTradesMax);
            map.put(username, userData);
        }
    }

    public static int getNumberOfTradesMax(String username){
        if(map.get(username) != null){
            UserData userData = map.get(username);
            return userData.getNumberOfTradesMax();
        }
        return 0;
    }

    public static void removeUserState(String Username){
        if(map.get(Username) != null){
            map.remove(Username);
        }
    }

    public static void setStopById(String username, int id){
        if(map.get(username) != null){
            UserData userData = map.get(username);
            userData.setStopById(id);
            map.put(username, userData);
        }
    }

    public static int getStopById(String username){
        if(map.get(username) != null){
            UserData userData = map.get(username);
            return userData.getStopById();
        }
        return 0;
    }

}

