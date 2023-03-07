import glob
import torch
from model import SRCNN
from datasets import TrainDataset
from torch.utils.data import DataLoader
from torch import nn
from torch import optim

device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
data_list = glob.glob('../datasets/train/img_align_celeba/img_align_celeba/*.jpg')

train_datasets = TrainDataset(data_list)
train_dataloader = DataLoader(train_datasets, batch_size=64)


def weights_init(m):
    classname = m.__class__.__name__
    if classname.find('conv') != -1:
        nn.init.normal_(model.weight.data, 0.0, 0.02)


model = SRCNN().to(device)
model.apply(weights_init)

loss_func = nn.MSELoss()
opt = optim.Adam(model.parameters())
epoch = 30


def train(m, data_ld):
    m.train()
    e_loss = 0.0

    for i, (img, label) in enumerate(data_ld):
        img = img.to(device)
        label = label.to(device)

        opt.zero_grad()
        out = model(img)
        loss = loss_func(out, label)
        loss.backward()
        opt.step()

        e_loss += loss.item()

    return e_loss / len(data_ld.dataset)


loss_list = list()

for e in range(epoch):
    epoch_loss = train(model, train_dataloader)
    print(f'epoch: {e+1} and epoch loss: {epoch_loss}')
    loss_list.append(epoch_loss)

torch.save(model, 'SRCNN30.pth')
