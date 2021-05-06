from pydantic import BaseModel
from typing import Optional
from enum import Enum


class ReactionsEnum(str, Enum):
    like = 'like'
    love = 'love'
    hate = 'hate'
    fuck_you = 'fuck you'


##############################################################
####################### request models #######################
##############################################################

class RequestLogin(BaseModel):
    username: str
    password: str

class RequestRegister(BaseModel):
    username: str
    first_name: str
    last_name: str
    email: str
    password: str
    
class RequestEditUser(BaseModel):
    username: Optional[str] = None
    first_name: Optional[str] = None
    last_name: Optional[str] = None
    email: Optional[str] = None
    password: Optional[str] = None
    avatar: Optional[str] = None

class RequestCreateGroup(BaseModel):
    name: str
    image: Optional[bytes] = None

class RequestAddUser(BaseModel):
    user_id: int

class RequestCreatePost(BaseModel):
    text: str

class RequestReaction(BaseModel):
    reaction: ReactionsEnum

class RequestCreateComment(BaseModel):
    text: str

class RequestCreateMessage(BaseModel):
    text: str
    
##############################################################
####################### response models ######################
##############################################################

# TODO