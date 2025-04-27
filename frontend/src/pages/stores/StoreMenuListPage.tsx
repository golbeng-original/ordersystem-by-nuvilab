import { Box, Button, Card, CardContent, FormControlLabel, Grid, Radio, RadioGroup, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import useStoreStores, { StoreEntity, StoreMenuItemEntity } from "../../features/stores/store/storeStores";
import useGetAddresses from "../../features/user/hooks/useGetAddresses";
import useRequestOrder, { OrderPayload } from "../../features/stores/hooks/useRequestOrder";

interface OrderForm {
    storeId: number;
    addressId: string;
    paymentMethod:String;
    menus:Record<string, number>;
}

const Counter = (props:{
    onHandleQuantityChange:(quantity:number) => void
}) => {

    const [count, setCount] = useState(0);

    useEffect(() => {
        props.onHandleQuantityChange(count);
    }, [count])

    return (
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <Button 
                variant="outlined" 
                size="small" 
                onClick={() => {
                    if( count - 1 < 0 ) {
                        return;
                    }
                    setCount(count - 1)
                }}
            >
            -
            </Button>
            <Typography>
                {count}
            </Typography>
            <Button 
                variant="outlined" 
                size="small" 
                onClick={() => setCount(count + 1)}
            >
              +
            </Button>
      </Box>
    )
}

const StoreMenuItem = (props:{
    menuItem:StoreMenuItemEntity,
    onUpdateMenus: (itemId:string, quantity:number) => void
}) => {

    const {onUpdateMenus} = props;

    const {id, name, desc, price} = props.menuItem;
    

    const onHandleQuantityChange = (quantity:number) => {
        onUpdateMenus(id, quantity);
    };

    return (
        <Grid key={id}>
            <Card variant="outlined">
                <CardContent>
                    <Typography variant="h6">{name}</Typography>
                    <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                        {desc}
                    </Typography>
                    <Typography variant="body1" color="primary">
                        {price.toLocaleString()} 원
                    </Typography>
                    <Counter onHandleQuantityChange={onHandleQuantityChange}/>
                 </CardContent>
            </Card>
        </Grid>
    )
}

const StoreMenuListBody = (prpos:{
    selectedStore:StoreEntity | undefined,
    onUpdateMenus: (itemId:string, quantity:number) => void,
    onUpdateAddress: (addressId:string) => void
}) => {
    const { selectedStore, onUpdateMenus, onUpdateAddress } = prpos;
    const { addresses } = useGetAddresses();
    const [selectedAddressId, setSelectedAddressId] = useState('');

    useEffect(() => {
        if( addresses.length === 0 ) {
            console.log('aasdfsd')
            return;
        }

        if( selectedAddressId.length > 0 ) {
            return;
        }

        setSelectedAddressId(addresses[0].id);
        onUpdateAddress(addresses[0].id);

    }, [addresses, selectedAddressId])

    const onHandleAddressChange = (addressId:string) => {
        if( addressId.length === 0 ) {
            return;
        }

        setSelectedAddressId(addressId);
        onUpdateAddress(addressId);
    }

    if( !selectedStore ) {
        return (
            <Box>
                <Typography variant="body1">상점이 존재 하지 않습니다.</Typography>
            </Box>
        );
    }

    return (
        <>
            <Typography variant="h4" gutterBottom>
                {selectedStore.name}
            </Typography>
            <Typography variant="h4" gutterBottom>
                {selectedStore.desc}
            </Typography>
            <Grid container spacing={2}>
                {selectedStore.menus.map((menu) => (
                    <StoreMenuItem key={menu.id} menuItem={menu} onUpdateMenus={onUpdateMenus}/>
                ))}
            </Grid>

            <Box sx={{ mt: 4, textAlign: 'left' }}>
                <Typography variant="h6" gutterBottom>
                    📍 배송지 선택
                </Typography>
                <RadioGroup
                    value={selectedAddressId} 
                    onChange={(e) => onHandleAddressChange(e.target.value)}
                >
                    {addresses.map((address) => (
                        <FormControlLabel
                          key={address.id}
                          value={address.id}
                          control={<Radio />}
                          label={address.address}
                        />
                    ))}
                </RadioGroup>
            </Box>
        </>
    );
}

const StoreMenuListPage = () => {

    const { storeId } = useParams();
    const navigate = useNavigate();

    const selectedStore = useStoreStores((state => {
        return state.stores.find(store => store.id === Number(storeId))
    }));

    const { orderId, requestOrder } = useRequestOrder();


    const [orderForm, setOrderForm] = useState<OrderForm>({
        storeId: Number(storeId),
        addressId: '',
        paymentMethod: 'CREDIT_CARD',
        menus: {}
    });

    useEffect(() => {
        if( orderId ) {
            navigate(`/order/${orderId}`)
        }
    }, [orderId])

    const onUpdateAddress = (addressId:string) => {

        setOrderForm((prev) => ({
            ...prev,
            addressId: addressId
        }))
    }

    const onUpdateMenus = (itemId:string, quantity:number) => {
        setOrderForm((prev) => {
            let prevMenu = {...prev.menus};
            prevMenu = {
                ...prevMenu,
                ...{
                    [itemId] : quantity
                }
            }

            if( quantity === 0 ) {
                delete prevMenu[itemId];
            }

            return {
                ...prev,
                menus: {...prevMenu}
            }

        });
    }

    const onHandleGoHome = () => {
        navigate('/stores');
    }

    const onHandlePayment = async () => {

        const useRequestOrder:OrderPayload = {
            storeId: orderForm.storeId,
            menuItems: Object.entries(orderForm.menus).map(
                ([menuId, quantity]) => ({
                    menuItemId: menuId,
                    quantity: quantity
                })
            ),
            receiveAddressId: orderForm.addressId,
            paymentMethod: 'Card'
        };

        await requestOrder(useRequestOrder);
    }

    return (
        <Box sx={{ 
            background: 'linear-gradient(to right, #667eea, #764ba2)',
            width: '100vw',
            height: '100vh',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            flexDirection: 'column',
        }}>
            <StoreMenuListBody 
                selectedStore={selectedStore} 
                onUpdateMenus={onUpdateMenus}
                onUpdateAddress={onUpdateAddress}
            />
            <Box sx={{ 
                display: 'flex',
                justifyContent: 'center',
                width: '100%',
            }}>
                <Button
                    variant="contained"
                    color="primary"
                    sx={{ mt: 2 }}
                    onClick={onHandleGoHome}
                >
                    뒤로 가기
                </Button>
                <Box sx={{ ml: 2 }} />
                <Button
                    variant="contained"
                    color="primary"
                    sx={{ mt: 2 }}
                    onClick={onHandlePayment}
                >
                    결제하기
                </Button>
            </Box>
        </Box>
    );
}

export default StoreMenuListPage;