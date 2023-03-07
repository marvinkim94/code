import torch
from torch.utils.data import DataLoader
from datasets import TrainDataset
import glob
import matplotlib.pyplot as plt
import torchvision.transforms as T

device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
sr_model = torch.load('SRCNN30.pth', map_location=device)

data_list = glob.glob('../datasets/train/img_align_celeba/img_align_celeba/*.jpg')
test_datasets = TrainDataset(data_list)
test_dataloader = DataLoader(test_datasets, batch_size=64, shuffle=True)

for img, label in test_dataloader:
    img = img[0]
    label = label[0]
    break

sr_model.eval()
with torch.no_grad():
    img_ = img.unsqueeze(0)
    img_ = img_.to(device)
    output = sr_model(img_)
    output = output.squeeze(0)

transform = T.ToPILImage()

blur_image = transform(img)
original = transform(label)
output_tested_image = transform(output)

plt.figure(figsize=(15,15))
plt.subplot(1,3,1)
plt.imshow(blur_image)
plt.title('original_blur')
plt.subplot(1,3,2)
plt.imshow(output_tested_image)
plt.title('output')
plt.subplot(1,3,3)
plt.imshow(original)
plt.title('original')
plt.show()
