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
    if months == 6:
        prices = prices[-9 * MONTH_WORK_DAYS:]
    else:
        prices = prices[-6 * MONTH_WORK_DAYS:]
    model = keras.models.load_model('../ml/compiled_models/' + ticker + "_" + str(months) + 'm.h5')
    input_arr = []
    prices_final = []
    for i in range(0, len(prices)):
        prices_final.append([prices[i]])
    scaler = MinMaxScaler(feature_range=(0, 1))
    scaled_data = scaler.fit_transform(prices_final)
    input_arr.append(scaled_data)
    input_arr = np.array(input_arr)
    input_arr = np.reshape(input_arr, (input_arr.shape[0], input_arr.shape[1], 1))
    closing_price = model.predict(input_arr)
    closing_price = scaler.inverse_transform(closing_price)
    return closing_price[0][0]

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