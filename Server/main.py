# external packages
from fastapi import FastAPI, Depends
from fastapi.security import OAuth2PasswordBearer, OAuth2PasswordRequestForm
# internal packages
from models import *
from auth import getCurrentUser
from comment import getComments
from group import getGroup, getUsers
from post import getPosts, getReactions as getReactionsPost
from chat import getChats, getReactions as getReactionsChat
from user import login, register, getGroupsAdmin, getGroupsMember, showProfile


##############################################################
################ utworzenie obiektu aplikacji ################
##############################################################

app = FastAPI(
    title='Grupkowo',
    description='Nowa lepsza wersja facebooka',
    version='0.69.420'
)


##############################################################
########################## end pointy ########################
##############################################################

# default
@app.get('/', summary='Default')
async def f01():
    return {
        'app-name': 'Grupkowo',
        'link': 'https://github.com/KamilMatejuk/Grupkowo'
    }


# /token
@app.post(
    '/token/',
    tags=['token'],
    summary='Wygenerowanie tokenu',
    description='Token jest dodatkowo generowany i zwracany przy logowaniu / rejestracji',
    response_model=ResponseToken)
async def f02(form: OAuth2PasswordRequestForm = Depends()):
    return login(form.username, form.password)


# /user/login
@app.post(
    '/user/login/',
    tags=['login'],
    summary='Zalogowanie uzytkownika',
    response_model=ResponseToken)
async def f1(form: RequestLogin):
    return login(form.name, form.password)


##############################################################
#################### uźytkownik (user.py) ####################
##############################################################

# /user/me
@app.post(
    '/user/me/',
    tags=['aktualny użytkownik'],
    summary='Zarejestrowanie użytkownika',
    response_model=ResponseToken)
async def f2(user: RequestRegister):
    return register(user)


@app.get(
    '/user/me/',
    tags=['aktualny użytkownik'],
    summary='Pobranie profilu',
    response_model=ResponseUserProfile)
async def f3(user = Depends(getCurrentUser)):
    return user


@app.put(
    '/user/me/',
    tags=['aktualny użytkownik'],
    summary='Edycja profilu')
async def f4(user: RequestEditUser):
    return {}


@app.delete(
    '/user/me/',
    tags=['aktualny użytkownik'],
    summary='Usunięcie profilu')
async def f5():
    return {}


# /user/groups-user/
@app.get(
    '/user/groups-user/',
    tags=['aktualny użytkownik'],
    summary='Pobranie listy grup do których należy użytkownik',
    response_model=ResponseUserGroups)
async def f6(user = Depends(getCurrentUser)):
    return getGroupsMember(user)


# /user/groups-admin/
@app.get(
    '/user/groups-admin/',
    tags=['aktualny użytkownik'],
    summary='Pobranie listy grup, których adminem jest użytkownik',
    response_model=ResponseUserGroups)
async def f7(user = Depends(getCurrentUser)):
    return getGroupsAdmin(user)


@app.post(
    '/user/groups-admin/',
    tags=['aktualny użytkownik'],
    summary='Utworzenie nowej grupy')
async def f8(group: RequestCreateGroup):
    return {}


# /user/{user_id}
@app.get(
    '/user/{user_id}/',
    tags=['inny użytkownik'],
    summary='Pobranie profilu innego użytkownika',
    response_model=ResponseCollegueProfile)
async def f9(user_id: int):
    return showProfile(user_id)


##############################################################
###################### grupa (group.py) ######################
##############################################################

# /group/{group_id}/
@app.get(
    '/group/{group_id}/',
    tags=['grupa'],
    summary='Pobranie danych grupy',
    response_model=ResponseGroup)
async def f10(group_id: int):
    return getGroup(group_id)


@app.delete(
    '/group/{group_id}/',
    tags=['grupa'],
    summary='Usunięcie grupy')
async def f11(group_id: int):
    return {}


# /group/{group_id}/users/
@app.get(
    '/group/{group_id}/users/',
    tags=['grupa'],
    summary='Pobranie listy użytkowników grupy',
    response_model=ResponseGroupUsers)
async def f12(group_id: int, user = Depends(getCurrentUser)):
    return getUsers(group_id, user)


@app.post(
    '/group/{group_id}/users/',
    tags=['grupa'],
    summary='Dodanie użytkownika do grupy')
async def f13(group_id: int, user: RequestAddUser):
    return {}


@app.delete(
    '/group/{group_id}/users/{user_id}',
    tags=['grupa'],
    summary='Usuniecie użytkownika z grupy')
async def f14(group_id: int):
    return {}


##############################################################
################# posty w grupie (post.py) ###################
##############################################################

# /group/{group_id}/posts/
@app.get(
    '/group/{group_id}/posts/',
    tags=['post'],
    summary='Pobranie ostatnich postów w grupie',
    response_model=ResponsePosts)
async def f15(group_id: int, user = Depends(getCurrentUser)):
    return getPosts(group_id, user)


@app.post(
    '/group/{group_id}/posts/',
    tags=['post'],
    summary='Dodanie posta w grupie')
async def f16(group_id: int, post: RequestCreatePost):
    return {}


# /group/{group_id}/posts/{timestamp_start}/{timestamp_end}/
@app.get(
    '/group/{group_id}/posts/{timestamp_start}-{timestamp_end}/',
    tags=['post'],
    summary='Pobranie listy postów w grupie z danego zakresu dat',
    description='"timestamp_start" i "timestamp_start" muszą być datą postaci DDMMYYYY',
    response_model=ResponsePosts)
async def f17(group_id: int, timestamp_start: str, timestamp_end: str, user = Depends(getCurrentUser)):
    return getPosts(group_id, user, start=timestamp_start, end=timestamp_end)


# /group/{group_id}/posts/{post_id}/
@app.put(
    '/group/{group_id}/posts/{post_id}/',
    tags=['post'],
    summary='Edycja posta')
async def f18(group_id: int, post_id: int, post: RequestCreatePost):
    return {}


@app.delete(
    '/group/{group_id}/posts/{post_id}/',
    tags=['post'],
    summary='Usunięcie posta')
async def f19(group_id: int, post_id: int):
    return {}


# /group/{group_id}/posts/{post_id}/reactions/
@app.get(
    '/group/{group_id}/posts/{post_id}/reactions/',
    tags=['post'],
    summary='Pobranie reakcji do posta',
    response_model=ResponseReactions)
async def f20(group_id: int, post_id: int, user = Depends(getCurrentUser)):
    return getReactionsPost(group_id, post_id, user)


@app.post(
    '/group/{group_id}/posts/{post_id}/reactions/',
    tags=['post'],
    summary='Dodanie reakcji do posta')
async def f21(group_id: int, post_id: int, reaction: RequestReaction):
    return {}


@app.delete(
    '/group/{group_id}/posts/{post_id}/reactions/',
    tags=['post'],
    summary='Usunięcie reakcji do posta')
async def f22(group_id: int, post_id: int):
    return {}


##############################################################
############# koemntarze do postów (comment.py) ##############
##############################################################

# /group/{group_id}/posts/{post_id}/comments/
@app.get(
    '/group/{group_id}/posts/{post_id}/comments/',
    tags=['komentarz'],
    summary='Pobranie komentarzy do posta',
    response_model=ResponseComments)
async def f23(group_id: int, post_id: int, user = Depends(getCurrentUser)):
    return getComments(group_id, post_id, user)


@app.post(
    '/group/{group_id}/posts/{post_id}/comments/',
    tags=['komentarz'],
    summary='Dodanie komentarza do posta')
async def f24(group_id: int, post_id: int, comment: RequestCreateComment):
    return {}


# /group/{group_id}/posts/{post_id}/comments/{comment_id}/
@app.put(
    '/group/{group_id}/posts/{post_id}/comments/{comment_id}/',
    tags=['komentarz'],
    summary='Edycja komentarza pod postem')
async def f25(group_id: int, post_id: int, comment_id: int, comment: RequestCreateComment):
    return {}


@app.delete(
    '/group/{group_id}/posts/{post_id}/comments/{comment_id}/',
    tags=['komentarz'],
    summary='Usunięcie komentarza pod postem')
async def f26(group_id: int, post_id: int, comment_id: int):
    return {}


##############################################################
############## wiadomości na chacie (chat.py) ################
##############################################################

# /group/{group_id}/chats/
@app.get(
    '/group/{group_id}/chats/',
    tags=['chat'],
    summary='Pobranie ostatnich wiadomości',
    response_model=ResponseMessages)
async def f27(group_id: int, user = Depends(getCurrentUser)):
    return getChats(group_id, user)


@app.post(
    '/group/{group_id}/chats/',
    tags=['chat'],
    summary='Wysłanie wiadomości')
async def f28(group_id: int, message: RequestCreateMessage):
    return {}


# /group/{group_id}/chats/{timestamp_start}-{timestamp_end}/
@app.get(
    '/group/{group_id}/chats/{timestamp_start}-{timestamp_end}/',
    tags=['chat'],
    summary='Pobranie wiadomości z danego zakresu dat',
    description='"timestamp_start" i "timestamp_start" muszą być datą postaci DDMMYYYY',
    response_model=ResponseMessages)
async def f29(group_id: int, timestamp_start: str, timestamp_end: str, user = Depends(getCurrentUser)):
    return getChats(group_id, user, start=timestamp_start, end=timestamp_end)


# /group/{group_id}/chats/{message_id}/reactions/
@app.get(
    '/group/{group_id}/chats/{message_id}/reactions/',
    tags=['chat'],
    summary='Pobranie reakcji do danej wiadomości',
    response_model=ResponseReactions)
async def f30(group_id: int, message_id: int, user = Depends(getCurrentUser)):
    return getReactionsChat(group_id, message_id, user)


@app.post(
    '/group/{group_id}/chats/{message_id}/reactions/',
    tags=['chat'],
    summary='Zareagowanie na wiadomość')
async def f31(group_id: int, message_id: int, reaction: RequestReaction):
    return {}


@app.delete(
    '/group/{group_id}/chats/{message_id}/reactions/',
    tags=['chat'],
    summary='Usunięcie reakcji na wiadomość')
async def f32(group_id: int, message_id: int):
    return {}

