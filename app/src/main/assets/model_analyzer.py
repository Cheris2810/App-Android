import tensorflow as tf

# Tải mô hình TensorFlow Lite
model_path = "Resnet_V2_Model.tflite"  # Đường dẫn đến mô hình của bạn
interpreter = tf.lite.Interpreter(model_path=model_path)
interpreter.allocate_tensors()

# Lấy thông tin về các tensor
input_details = interpreter.get_input_details()
output_details = interpreter.get_output_details()

print("Input Details:")
for detail in input_details:
    print(detail)

print("\nOutput Details:")
for detail in output_details:
    print(detail)
