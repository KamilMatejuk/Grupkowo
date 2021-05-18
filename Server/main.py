# external packages
from fastapi import FastAPI, Depends, status
from fastapi.security import OAuth2PasswordRequestForm
# internal packages
from models import *
from auth import getCurrentUser
from comment import getComments, addComment, editComment, deleteComment
from group import deleteGroup, getGroup, getUsers, createGroup, deleteGroup, addUser, deleteUser
from post import getPosts, getReactions as getReactionsPost, createPost, editPost, deletePost, addReaction as addReactionPost, deleteReaction as deleteReactionPost
from chat import getMessages, getReactions as getReactionsChat, addMessage, addReaction as addReactionMessage, deleteReaction as deleteReactionMessage
from user import login, register, getGroupsAdmin, getGroupsMember, showProfile, editProfile, deleteProfile


##############################################################
################ utworzenie obiektu aplikacji ################
##############################################################

app = FastAPI(
    title='Grupkowo',
    description='Nowa lepsza wersja facebooka',
    version='0.69.420'
)

successfulResponse = {
    'successfull': True
}


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
    summary='Edycja profilu',
    status_code=status.HTTP_200_OK)
async def f4(newUser: RequestEditUser, currUser = Depends(getCurrentUser)):
    return editProfile(newUser, currUser)


@app.delete(
    '/user/me/',
    tags=['aktualny użytkownik'],
    summary='Usunięcie profilu',
    status_code=status.HTTP_200_OK)
async def f5(user = Depends(getCurrentUser)):
    return deleteProfile(user)


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
    summary='Utworzenie nowej grupy',
    status_code=status.HTTP_200_OK)
async def f8(group: RequestCreateGroup, user = Depends(getCurrentUser)):
    return createGroup(group, user)


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
    summary='Usunięcie grupy',
    status_code=status.HTTP_200_OK)
async def f11(group_id: int, user = Depends(getCurrentUser)):
    return deleteGroup(group_id, user)


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
    summary='Dodanie użytkownika do grupy',
    status_code=status.HTTP_200_OK)
async def f13(group_id: int, user: RequestAddUser, currUser = Depends(getCurrentUser)):
    return addUser(group_id, user, currUser)


@app.delete(
    '/group/{group_id}/users/{user_id}/',
    tags=['grupa'],
    summary='Usuniecie użytkownika z grupy',
    status_code=status.HTTP_200_OK)
async def f14(group_id: int, user_id: int, user = Depends(getCurrentUser)):
    return deleteUser(group_id, user_id, user)


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
    summary='Dodanie posta w grupie',
    status_code=status.HTTP_200_OK)
async def f16(group_id: int, post: RequestCreatePost, user = Depends(getCurrentUser)):
    return createPost(group_id, post, user)


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
    summary='Edycja posta',
    status_code=status.HTTP_200_OK)
async def f18(group_id: int, post_id: int, newPost: RequestCreatePost, user = Depends(getCurrentUser)):
    return editPost(group_id, post_id, newPost, user)


@app.delete(
    '/group/{group_id}/posts/{post_id}/',
    tags=['post'],
    summary='Usunięcie posta',
    status_code=status.HTTP_200_OK)
async def f19(group_id: int, post_id: int, user = Depends(getCurrentUser)):
    return deletePost(group_id, post_id, user)


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
    summary='Dodanie reakcji do posta',
    status_code=status.HTTP_200_OK)
async def f21(group_id: int, post_id: int, reaction: RequestReaction, user = Depends(getCurrentUser)):
    return addReactionPost(group_id, post_id, reaction, user)


@app.delete(
    '/group/{group_id}/posts/{post_id}/reactions/',
    tags=['post'],
    summary='Usunięcie reakcji do posta',
    status_code=status.HTTP_200_OK)
async def f22(group_id: int, post_id: int, user = Depends(getCurrentUser)):
    return deleteReactionPost(group_id, post_id, user)


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
    summary='Dodanie komentarza do posta',
    status_code=status.HTTP_200_OK)
async def f24(group_id: int, post_id: int, comment: RequestCreateComment, user = Depends(getCurrentUser)):
    return addComment(group_id, post_id, comment, user)


# /group/{group_id}/posts/{post_id}/comments/{comment_id}/
@app.put(
    '/group/{group_id}/posts/{post_id}/comments/{comment_id}/',
    tags=['komentarz'],
    summary='Edycja komentarza pod postem',
    status_code=status.HTTP_200_OK)
async def f25(group_id: int, post_id: int, comment_id: int, newComment: RequestCreateComment, user = Depends(getCurrentUser)):
    return editComment(group_id, post_id, comment_id, newComment, user)


@app.delete(
    '/group/{group_id}/posts/{post_id}/comments/{comment_id}/',
    tags=['komentarz'],
    summary='Usunięcie komentarza pod postem',
    status_code=status.HTTP_200_OK)
async def f26(group_id: int, post_id: int, comment_id: int, user = Depends(getCurrentUser)):
    return deleteComment(group_id, post_id, comment_id, user)


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
    return getMessages(group_id, user)


@app.post(
    '/group/{group_id}/chats/',
    tags=['chat'],
    summary='Wysłanie wiadomości',
    status_code=status.HTTP_200_OK)
async def f28(group_id: int, message: RequestCreateMessage, user = Depends(getCurrentUser)):
    return addMessage(group_id, message, user)


# /group/{group_id}/chats/{timestamp_start}-{timestamp_end}/
@app.get(
    '/group/{group_id}/chats/{timestamp_start}-{timestamp_end}/',
    tags=['chat'],
    summary='Pobranie wiadomości z danego zakresu dat',
    description='"timestamp_start" i "timestamp_start" muszą być datą postaci DDMMYYYY',
    response_model=ResponseMessages)
async def f29(group_id: int, timestamp_start: str, timestamp_end: str, user = Depends(getCurrentUser)):
    return getMessages(group_id, user, start=timestamp_start, end=timestamp_end)


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
    summary='Zareagowanie na wiadomość',
    status_code=status.HTTP_200_OK)
async def f31(group_id: int, message_id: int, reaction: RequestReaction, user = Depends(getCurrentUser)):
    return addReactionMessage(group_id, message_id, reaction, user)


@app.delete(
    '/group/{group_id}/chats/{message_id}/reactions/',
    tags=['chat'],
    summary='Usunięcie reakcji na wiadomość',
    status_code=status.HTTP_200_OK)
async def f32(group_id: int, message_id: int, user = Depends(getCurrentUser)):
    return deleteReactionMessage(group_id, message_id, user)

