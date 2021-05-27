import sys
import numpy as np
from sklearn.preprocessing import MinMaxScaler
from tensorflow import keras

def write_result(res):
    with open("../ml/output.txt", "w+") as out:
        out.write(str(res))
    print('finished')

MONTH_WORK_DAYS = 21

def call_model(ticker, prices, months):
    model = keras.models.load_model('../ml/compiled_models/' + ticker + "_" + str(months) + 'm.h5')
    input_arr = []
    prices_final = []
    for i in range(0, len(prices)):
        prices_final.append([prices[i]])
    scaler = MinMaxScaler(feature_range=(0, 1))
    scaled_data = scaler.fit_transform(prices_final)
    predictFuture = scaled_data[-6 * MONTH_WORK_DAYS:]
    predictCurrent = scaled_data[(-6-months) * MONTH_WORK_DAYS : -months * MONTH_WORK_DAYS]
    input_arr.append(predictCurrent)
    input_arr.append(predictFuture)
    input_arr = np.array(input_arr)
    input_arr = np.reshape(input_arr, (input_arr.shape[0], input_arr.shape[1], 1))
    closing_price = model.predict(input_arr)
    closing_price = scaler.inverse_transform(closing_price)
    diff = closing_price[0][0]-prices[len(prices)-1]
    return closing_price[1][0]-diff

def read_input():
    reader = open('../ml/input.txt')
    try:
        lines = reader.readlines()
        ticker = lines[0].rstrip()
        months = int(lines[1].rstrip())
        prices_str = lines[2].rstrip().split(' ')
        prices = []
        for price in prices_str:
            prices.append(float(price))
        return ticker, prices, months
    except BaseException:
        with open("../ml/output.txt", "w+") as out:
            out.write('error reading input file')
        print('error')
        sys.exit('error reading input file')
    finally:
        reader.close()


def make_prediction():
    ticker, prices, months = read_input()
    res = call_model(ticker, prices, months)
    write_result(res)

make_prediction()