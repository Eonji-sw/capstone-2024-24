import React, { useState } from "react";
import {
    Grid,
    TextField,
    Button,
    Chip,
    Paper,
    Table,
    TableBody,
    TableRow,
    TableCell,
    DialogTitle,
    DialogContent,
    DialogActions,
    Dialog,
} from "@material-ui/core";
import PageTitle from "../../components/PageTitle";
import Widget from "../../components/Widget";
import useStyles from "./styles";
import CollectionsIcon from "@material-ui/icons/Collections";

const initialStore = {
    logo: '/Midoin/MidoinLogo.jpeg',
    name: '미도인',
    description: '강남점',
    aiDescription:"먹는 즐거움과 보는 즐거움, 머무는 즐거움으로 만족을 드리는 ‘미도인’ 인간적이면서도 더할 나위 없이 행복한 사람들과 함께 하고자, 런칭한 브랜드로 오래도록 고객들에게 사랑받고, 점주 또한 오랜 기간을 운영할 수 있는 장수브랜드의 모범이 되고자 합니다.\n" +
        "\n" +
        "\n" +
        "과거와 현재, 동양과 서양이 공존하는 공간으로 젋은 연인들의 데이트 장소나 친구들과의 모임, 특별한 날에 찾아볼 만한 공간으로 자리매김하고 있는 ‘미도인’은 공간의 비일상성과 비정형, 입체감이 있는 특유의 콘셉트로 젋은이들의 ‘사진맛집’으로 자리잡고 있습니다.\n" +
        "\n" +
        "\n" +
        "‘가정식 스테이크 전문점의 <맛>’맛에 대한 고민과 수 많은 노력끝에 가정식 스테이크전문점을 표방하면서도 고객들이 쉽게 즐길 수 없는 다양한 메뉴들을 구성했습니다.\n" +
        "\n" +
        "\n" +
        "온갖 정성이 들어간 한정 스페셜 메뉴인 ‘미도인 구첩 반상’과 ‘400 스테이크 덮밥’과 미도인 스테이크, 가정식 등심 스테이크, 트리플 스테이크, 대창 부채 스테이크, 곱창등심 스테이크, 미도인 스테이크 덮밥, 대창 덮밥, 곱창대창 덮밥, 곱창대창큐브 스테이크 덮밥은 맛을 기본으로 가격대가 비싸지 않고 양이 푸짐해 고객분들의 만족도를 높이고 있습니다.",
    date: '2024-03-20',
    categories: ["직원 호출", "스테이크류", "덮밥류", "면류", "사이드 메뉴", "음료 메뉴", "주류 메뉴"]

}
const initialCategories = initialStore.categories

export default function StoreManagement() {
    const classes = useStyles();
    // useState 훅을 사용하여 매장데이터 상태를 관리합니다.
    const [storeData, setStoreData] = useState(initialStore)
    // 업데이트할 storeData 내용
    const [storeUpdateData, setStoreUpdateData] = useState(initialStore)
    // 카테고리 관리를 위한 상태
    const [categories, setCategories] = useState(initialCategories);
    // 업데이트할 categories 내용
    const [updateCategories, setUpdateCategories] = useState(initialCategories);
    const [newCategory, setNewCategory] = useState('');
    const [open, setOpen] = useState(false); // 모달 상태
    const [newImage, setNewImage] = useState(initialStore.logo)
    // 저장 버튼을 눌렀을 때 실행될 함수입니다.
    const handleSave = () => {
        if(window.confirm("저장하시겠습니까?")) {
            storeData.categories = updateCategories
            setStoreData(storeUpdateData)
            setCategories(updateCategories)
            setOpen(false)
        }
        // todo : API 호출을 통해 서버에 데이터를 저장
    };
    const handleImageChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onloadend = () => {
                setNewImage(reader.result);
            };
            reader.readAsDataURL(file);
        }
    };
    // 카테고리 추가
    const addCategory = () => {
        if (newCategory && !updateCategories.includes(newCategory)) {
            setUpdateCategories([...updateCategories, newCategory]);
            setNewCategory('');
        }
    };
    // 카테고리 삭제
    const deleteCategory = (categoryToDelete) => {
        setUpdateCategories(updateCategories.filter(category => category !== categoryToDelete));
    };
    // 모달 닫기
    const handleModalOpen = () => {
        setOpen(true);
    };
    // 모달 닫기
    const handleModalClose = () => {
        setStoreUpdateData(storeData)
        setUpdateCategories(categories)
        setNewCategory('')
        setOpen(false);
    };
    const handleUpdateChange = (e) => {
        const { name, value } = e.target;
        setStoreUpdateData({ ...storeUpdateData, [name]: value });
    };
    return (
        <div>
            <PageTitle title="매장 관리하기" />
            <Grid container spacing={4}>
                <Grid item xs={12}>
                    <Widget disableWidgetMenu>
                        <input
                            accept="image/*"
                            style={{display: 'none'}}
                            id="raised-button-file"
                            name="logo"
                            type="file"
                            onChange={handleImageChange}
                        />
                        <div className={classes.flexContainer}>
                            <div className={classes.imageContainer}>
                                {/* 이미지 미리보기 */}
                                <div className={classes.image}
                                     style={{
                                         backgroundImage: `url(${storeData.logo})`,
                                     }}
                                >
                                    {!storeData.logo && '이미지 업로드'}
                                </div>
                            </div>
                            <Paper className={classes.tableContainer}>
                                <div className={classes.tableSection}>
                                    <Table className={classes.table} aria-label="simple table">
                                        <TableBody>
                                            <TableRow>
                                                <TableCell align="center"
                                                           className={classes.tableHeaderCell}>매장명</TableCell>
                                                <TableCell align="center">{storeData.name}</TableCell>
                                                <TableCell align="center"
                                                           className={classes.tableHeaderCell}>지점</TableCell>
                                                <TableCell align="center">{storeData.description}</TableCell>
                                            </TableRow>
                                        </TableBody>
                                    </Table>
                                </div>
                                <div className={classes.aiTextSection}>
                                    {storeData.aiDescription}
                                </div>
                            </Paper>
                        </div>
                        <Paper
                            style={{
                                marginTop: "20px"
                            }}
                        >
                            <div className={classes.tableSection}>
                                <Table className={classes.table} aria-label="simple table">
                                    <TableBody>
                                        <TableRow>
                                            <TableCell align="center"
                                                       className={classes.tableHeaderCell}
                                                       style={{width: '30%'}}>메뉴 카테고리</TableCell>
                                            <TableCell align="left"
                                                       style={{width: '70%'}}>
                                                {categories.map((category, index) => (
                                                    <Chip
                                                        key={index}
                                                        label={category}
                                                        color="primary"
                                                        style={{margin: '5px'}}
                                                    />
                                                ))}
                                            </TableCell>
                                        </TableRow>
                                    </TableBody>
                                </Table>
                            </div>
                        </Paper>

                        {/* 저장 버튼 */}
                        <div className={classes.rightAlignContainer}>
                            <Button
                                variant="contained"
                                color="primary"
                                onClick={handleModalOpen}
                            >
                                수정하기
                            </Button>
                        </div>
                    </Widget>
                </Grid>
            </Grid>
            <Dialog open={open} onClose={handleModalClose} aria-labelledby="form-dialog-title">
                <DialogTitle id="form-dialog-title">매장 관리하기</DialogTitle>
                <DialogContent>
                    {/* 이미지 업로드 필드 */}
                    <div style={{
                        width: "100%",
                        display: "flex",
                        justifyContent: "center", // 수평 중앙 정렬
                    }}>
                        <input
                            accept="image/*"
                            style={{display: 'none'}}
                            id="raised-button-file"
                            name="logo"
                            type="file"
                            onChange={handleImageChange}
                        />
                        <div className={classes.newImageContainer}>
                            {/* 이미지 미리보기 */}
                            <div
                                className={classes.newImage}
                                style={{
                                    backgroundImage: `url(${newImage})`,
                                }}
                            >
                                {!newImage && '이미지 업로드'}
                            </div>
                            {/* 이미지 업로드 버튼 */}
                            <label htmlFor="raised-button-file" className={classes.uploadBtn}>
                                <Button variant="contained" component="span" style={{padding: 0, minWidth: 0}}>
                                    <CollectionsIcon/>
                                </Button>
                            </label>
                        </div>
                    </div>

                    <TextField
                        margin="dense"
                        name="name"
                        label="매장명"
                        type="text"
                        variant="outlined"
                        value={storeUpdateData.name}
                        fullWidth
                        onChange={handleUpdateChange}
                        className={classes.textFieldCustom}
                    />
                    <TextField
                        margin="dense"
                        name="description"
                        label="지점"
                        type="text"
                        variant="outlined"
                        value={storeUpdateData.description}
                        fullWidth
                        onChange={handleUpdateChange}
                        className={classes.textFieldCustom}
                    />
                    <TextField
                        margin="dense"
                        label="주미에서 매장을 설명해주세요."
                        type="text"
                        multiline
                        rows={6}
                        variant="outlined"
                        name="aiDescription"
                        value={storeUpdateData.aiDescription}
                        onChange={handleUpdateChange}
                        fullWidth
                        className={classes.textFieldCustom}
                    />
                    <TextField
                        label="새 카테고리"
                        variant="outlined"
                        value={newCategory}
                        onChange={(e) => setNewCategory(e.target.value)}
                        style={{marginBottom: 16, marginTop: 20}}
                    />
                    <Button onClick={addCategory} variant="contained" color="primary"
                            style={{marginBottom: 16, marginTop: 30, marginLeft: 10}}>
                        카테고리 추가
                    </Button>
                    <div>
                        {updateCategories.map((category, index) => (
                            <Chip
                                key={index}
                                label={category}
                                onDelete={() => deleteCategory(category)}
                                color="primary"
                                style={{margin: '5px'}}
                            />
                        ))}
                    </div>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleModalClose} color="secondary">
                        취소하기
                    </Button>
                    <Button onClick={handleSave} color="primary">
                        저장하기
                    </Button>
                </DialogActions>
            </Dialog>
        </div>
    );
}
