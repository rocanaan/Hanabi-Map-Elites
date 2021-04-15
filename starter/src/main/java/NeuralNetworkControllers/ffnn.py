import numpy as np
import torch
import torch.nn as nn
import torch.nn.functional as F
import sys
from torch.nn.parameter import Parameter


class Net(nn.Module):

    def __init__(self):
        super(Net, self).__init__()

        self.fc1 = nn.Linear(5,5) 
        self.fc1.weight.requires_grad=False
        self.fc1.bias.requires_grad=False
        # self.fc
        # self.fc2 = nn.Linear(5, 5)
        # self.fc3 = nn.Linear(5, 5)

        # self.layers = [self.fc1,self.fc2,self.fc3]

        # for l in self.layers:
        #     l.weight.requires_grad = False
        #     l.bias.requires_grad = False


    def forward(self, x):
        x = F.relu(self.fc1(x))
        x = F.relu(self.fc2(x))
        x = F.softmax(self.fc3(x))
        return x

    # def num_flat_features(self, x):
    #     size = x.size()[1:]  # all dimensions except the batch dimension
    #     num_features = 1
    #     for s in size:
    #         num_features *= s
    #     return num_features

    def num_parameters(self):
        # model_parameters = filter(lambda p: p.requires_grad, self.parameters())
        model_parameters = self.parameters()
        params = sum([np.prod(p.size()) for p in model_parameters])
        return params

    def set_parameters(self, params, num_parameters):
        index = 0

        # for l in self.layers:
        #     layer_size = l.weight.data.size()
        #     print(" size " + str(layer_size))
        #     bias_size = l.bias.data.size()
        #     print(bias_size)

        #     new_layer_weights = torch.tensor([params[index+j] for j in range(layer_size)])
        #     index+=layer_size
        #     new_bias_weights = [params[index+j]/num_parameters for j in range(bias_size)]
        #     index+=bias_size

        #     l.weight.data=new_layer_weights
        #     l.bias.data = new_data_weights

        # print("Finished updating " + str(index) + " weights")


        # ### Old version
        for p in self.parameters():
            start = index
            layersize = np.prod(p.size())
            new_layer_params = [(params[start+j]) for j in range(layersize)]
            # new_layer_params = torch.ones(layersize)

            index+=layersize
            p.data = Parameter(torch.tensor(new_layer_params))
            # print(p)
            # print(new_p)
            # p=new_p
            # print(p)

    def print_parameters(self):
        for p in self.parameters():
            print(p)




# def set_weights(net, weights):


net = Net()
n = net.num_parameters()
sys.stdout.write(str(n) + '\n')
sys.stdout.flush()



